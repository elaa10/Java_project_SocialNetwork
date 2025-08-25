package com.example.map_toysocialnetwork.service;

import com.example.map_toysocialnetwork.domain.Friendship;
import com.example.map_toysocialnetwork.domain.Tuple;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.repository.UserRepository;
import com.example.map_toysocialnetwork.utils.observer.Observable;
import com.example.map_toysocialnetwork.utils.observer.Observer;
import com.example.map_toysocialnetwork.repository.Repository;
import com.example.map_toysocialnetwork.utils.paging.Page;
import com.example.map_toysocialnetwork.utils.paging.Pageable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.StreamSupport;

public class Service implements Observable {

    private UserRepository repoUser;
    private Repository<Tuple<Long,Long>, Friendship> repoFriendship;
    List<Observer> observers = new ArrayList<>();


    public Service(UserRepository repoUser, Repository<Tuple<Long,Long>, Friendship> repoFriendship) {
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
    }

    public User findUser(String username) {
        for (User user : repoUser.findAll()) {
            if (Objects.equals(user.getUsername(), username)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(User user) {
        try {
            Optional<User> savedUser = repoUser.save(user);
            if (savedUser.isPresent()) {
                throw new ServiceException("Exista deja un User cu acest username");
            }
            notifyAllObservers();
        } catch (Exception e) {
            throw new ServiceException("Eroare la adăugarea utilizatorului: " + e.getMessage(), e);
        }
    }

    public void removeUser(String username) {
        Long id = findID(username);
        Optional<User> u = repoUser.findOne(id);
        if (u.isEmpty()) {
            throw new ServiceException("Nu exista niciun User cu acest id");
        }
        try{
            StreamSupport.stream(repoFriendship.findAll().spliterator(), false)
                    .filter(p -> p.getId().getLeft().equals(id) || p.getId().getRight().equals(id))
                    .forEach(p -> repoFriendship.delete(p.getId()));
            repoUser.delete(id);
            notifyAllObservers();
        }
        catch (Exception e) {
            throw new ServiceException("Eroare la ștergerea utilizatorului: " + e.getMessage(), e);
        }
    }

    private Long findID(String username) {
        for(User u : repoUser.findAll()) {
            if(u.getUsername().equals(username)) {
                return u.getId();
            }
        }
        return null;
    }

    public void updateUser(String username, User user) {
        Long id = findID(username);
        Optional<User> u = repoUser.findOne(id);
        if (u.isEmpty()) {
            throw new ServiceException("Nu exista niciun User cu acest id");
        }
        try{
            user.setId(id);
            repoUser.update(user);
            notifyAllObservers();
        }
        catch (Exception e) {
            throw new ServiceException("Eroare la actualizarea utilizatorului: " + e.getMessage(), e);
        }
    }

    public boolean verifyLogin(String username, String password) {
        Long idUser = findID(username);
        if(idUser == null) throw new ServiceException("Utilizatorul nu exista");
        Optional<User> user = repoUser.findOne(idUser);
        return Objects.equals(user.get().getPassword(), password);
    }

    public Iterable<User> getAllUsers() {

        return repoUser.findAll();
    }

    public Iterable<User> searchUsers(String name) {
        List<User> users = new ArrayList<>();
        for (User u : repoUser.findAll()) {
            if (u.getUsername().toLowerCase().contains(name.toLowerCase())) {
                users.add(u);
            }
        }
        return users;
    }

    public Iterable<User> getAllFriends(User user) {
        List<User> friends = new ArrayList<>();
        for(Friendship f : repoFriendship.findAll()) {
            if(f.getId().getLeft().equals(user.getId())) {
                friends.add(repoUser.findOne(f.getId().getRight()).orElse(null));
            }
            else if(f.getId().getRight().equals(user.getId())) {
                friends.add(repoUser.findOne(f.getId().getLeft()).orElse(null));
            }
        }
        return friends;
    }


    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyAllObservers() throws SQLException {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public void addFriendship(String username1, String username2) {
        Long id1 = findID(username1);
        Long id2 = findID(username2);
        Friendship p = null;
        Optional<User> u1 = repoUser.findOne(id1);
        Optional<User> u2 = repoUser.findOne(id2);
        if (u1.isEmpty() || u2.isEmpty()) {
            throw new ServiceException("Unul sau ambii Useri nu există, prietenia nu poate fi creată");
        }

        Friendship friendship = new Friendship(new Tuple<>(id1, id2));
        try {
            Optional<Friendship> existingFriendship1 = repoFriendship.findOne(new Tuple<>(id1, id2));
            Optional<Friendship> existingFriendship2 = repoFriendship.findOne(new Tuple<>(id2, id1));

            if (existingFriendship1.isPresent() || existingFriendship2.isPresent()) {
                throw new ServiceException("Prietenia deja există");
            }

            repoFriendship.save(friendship);
            notifyAllObservers();
        } catch (Exception e) {
            throw new ServiceException("Eroare la adăugarea prieteniei: " + e.getMessage(), e);
        }
    }

    public void removeFriendship(String username1, String username2) {
        Long id1 = findID(username1);
        Long id2 = findID(username2);
        Optional<Friendship> friendship = repoFriendship.findOne(new Tuple<>(id1, id2));
        if (friendship.isEmpty()) {
            friendship = repoFriendship.findOne(new Tuple<>(id2, id1));
        }
        if (friendship.isEmpty()) {
            throw new ServiceException("Nu există prietenie între acești utilizatori");
        }

        try {
            repoFriendship.delete(friendship.get().getId());
            notifyAllObservers();
        } catch (Exception e) {
            throw new ServiceException("Eroare la ștergerea prieteniei: " + e.getMessage(), e);
        }
    }


    public Iterable<Friendship> getAllFriendships() {

        return repoFriendship.findAll();
    }


    public int nrCommunities(){
        Map<Long, List<Long>> matrice_adiacenta = new HashMap<>();
        repoUser.findAll().forEach(u ->  matrice_adiacenta.put(u.getId(), new ArrayList<>()));

        repoFriendship.findAll().forEach(f -> {
            matrice_adiacenta.get(f.getId().getLeft()).add(f.getId().getRight());
            matrice_adiacenta.get(f.getId().getRight()).add(f.getId().getLeft());
        });

        Set<User> set_users = new HashSet<>();
        final int[] count = {0};

        repoUser.findAll().forEach(u -> {
            if (!set_users.contains(u)) {
                count[0]++;
                DFS(matrice_adiacenta, u, set_users);
            }
        });
        return count[0];
    }

    /**
     * DFS algorithm
     * @return lista cu toti Userii gasiti prin DFS
     */
    private List<User> DFS(Map<Long, List<Long>> matrice_adiacenta, User u, Set<User> set) {
        List<User> list = new ArrayList<>();
        list.add(u);
        set.add(u);

        matrice_adiacenta.get(u.getId()).forEach(f -> {
            User neighbor = repoUser.findOne(f).orElse(null);
            // If the neighbor exists and is not in the set, perform DFS
            if (neighbor != null && !set.contains(neighbor)) {
                List<User> l = DFS(matrice_adiacenta, neighbor, set);
                list.addAll(l);
            }
        });

        return list;
    }

    /**
     * @return lista cu toate comunitatile
     */
    public List<List<User>> getAllCommunities() {
        Map<Long, List<Long>> matrice_adiacenta = new HashMap<>();
        repoUser.findAll().forEach(u -> matrice_adiacenta.put(u.getId(), new ArrayList<>()));

        repoFriendship.findAll().forEach(f -> {
            matrice_adiacenta.get(f.getId().getLeft()).add(f.getId().getRight());
            matrice_adiacenta.get(f.getId().getRight()).add(f.getId().getLeft());
        });

        Set<User> set = new HashSet<>();
        List<List<User>> l = new ArrayList<>();

        repoUser.findAll().forEach(u -> {
            if (!set.contains(u))
                l.add(DFS(matrice_adiacenta, u, set));
        });
        return l;
    }

    /**
     * Returns the most sociable community
     * the most sociable community is the community of users with the longest path
     *
     * @return an Iterable of all the most sociable communities users
     */
    public List<Iterable<User>> mostSociableCommunity() {
        List<Iterable<User>> list = new ArrayList<>();
        Set<User> set = new HashSet<>();

        Map<Long, List<Long>> matrice_adiacenta = new HashMap<>();

        repoUser.findAll().forEach(u -> matrice_adiacenta.put(u.getId(), new ArrayList<>()));

        repoFriendship.findAll().forEach(f -> {
            matrice_adiacenta.get(f.getId().getLeft()).add(f.getId().getRight());
            matrice_adiacenta.get(f.getId().getRight()).add(f.getId().getLeft());
        });

        final int[] max = {-1};
        repoUser.findAll().forEach(u -> {
            if(!set.contains(u))
            {
                List<User> aux = DFS(matrice_adiacenta, u, set);
                int l = longestPath(aux);
                if(l > max[0])
                {
                    list.clear();
                    list.add(aux);
                    max[0] = l;
                }
                else if(l == max[0])
                    list.add(aux);
            }
        });

        return list;
    }

    /**
     * We look for the longest path in the community
     *
     * @param nodes - a list of all the users in a community
     *
     * @return the longest path
     */
    private int longestPath(List<User> nodes) {

        final int[] max = {0};

        nodes.forEach(u -> {
            int l = longestPathFromSource(u);
            if(max[0] < l)
                max[0] = l;
        });

        return max[0];
    }

    // this function is used to initialise the set
    private int longestPathFromSource(User source) {
        Set<User> set = new HashSet<>();
        return lee(source, set);
    }

    /**
     * lee's algorithm on a graph
     *
     * @param source - the source node (or user)
     * @param set - we need a map/set or frequency vector to keep track of the nodes we went through previously
     *
     * @return the longest path from the node source
     */
    private int lee(User source, Set<User> set) {
        Map<Long, List<Long>> matrice_adiacenta = new HashMap<>();
        repoUser.findAll().forEach(u -> matrice_adiacenta.put(u.getId(), new ArrayList<>()));

        repoFriendship.findAll().forEach(f -> {
            matrice_adiacenta.get(f.getId().getLeft()).add(f.getId().getRight());
            matrice_adiacenta.get(f.getId().getRight()).add(f.getId().getLeft());
        });

        final int[] max = {-1};
        matrice_adiacenta.get(source.getId()).forEach(f -> {
            if(!set.contains(repoUser.findOne(f).orElse(null)))
            {
                set.add(repoUser.findOne(f).orElse(null));
                int l = lee(repoUser.findOne(f).orElse(null), set);
                if(l > max[0])
                    max[0] = l;
                set.remove(repoUser.findOne(f).orElse(null));
            }
        });

        return max[0] + 1;
    }

    public Page<User> findAllOnPage(Pageable pageable, Long user_id) {
        return repoUser.findAllOnPage(pageable, user_id);
    }

}
