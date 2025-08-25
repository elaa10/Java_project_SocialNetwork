package com.example.map_toysocialnetwork.service;

import com.example.map_toysocialnetwork.domain.Friendship;
import com.example.map_toysocialnetwork.domain.FriendshipRequest;
import com.example.map_toysocialnetwork.domain.Tuple;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.repository.Repository;
import com.example.map_toysocialnetwork.repository.dataBase.FriendshipDataBaseRepository;
import com.example.map_toysocialnetwork.repository.dataBase.RequestDataBaseRepository;
import com.example.map_toysocialnetwork.repository.dataBase.UserDataBaseRepository;
import com.example.map_toysocialnetwork.utils.observer.Observable;
import com.example.map_toysocialnetwork.utils.observer.Observer;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipRequestService implements Observable {

    private Repository<Long, User> repoUser;
    private Repository<Tuple<Long,Long>, Friendship> repoFriendship;
    private Repository<Tuple<Long,Long>, FriendshipRequest> repoRequest;
    List<Observer> observers = new ArrayList<>();


    public FriendshipRequestService(Repository<Long, User> repoUser, Repository<Tuple<Long,Long>, Friendship> repoFriendship, Repository<Tuple<Long,Long>, FriendshipRequest> repoRequest) {
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
        this.repoRequest = repoRequest;
    }
    private Long findID(String username) {
        for(User u : repoUser.findAll()) {
            if(u.getUsername().equals(username)) {
                return u.getId();
            }
        }
        return null;
    }

    public void addRequest(String username1, String username2) {
        Long id1 = findID(username1);
        Long id2 = findID(username2);
        FriendshipRequest p = null;
        Optional<User> u1 = repoUser.findOne(id1);
        Optional<User> u2 = repoUser.findOne(id2);
        if (u1.isEmpty() || u2.isEmpty()) {
            throw new ServiceException("Unul sau ambii Useri nu există, cererea nu poate fi creată");
        }

        FriendshipRequest request = new FriendshipRequest(new Tuple<>(id1, id2));
        try {
            Optional<FriendshipRequest> existingFriendshipRequest1 = repoRequest.findOne(new Tuple<>(id1, id2));
            Optional<FriendshipRequest> existingFriendshipRequest2 = repoRequest.findOne(new Tuple<>(id2, id1));

            if (existingFriendshipRequest1.isPresent() || existingFriendshipRequest2.isPresent()) {
                throw new ServiceException("Prietenia deja există");
            }
            request.setStatus("new");
            repoRequest.save(request);
            notifyAllObservers();
        } catch (Exception e) {
            throw new ServiceException("Eroare la adăugarea cererii de prietenie: " + e.getMessage(), e);
        }
    }

    public void removeRequest(String username1, String username2) {
        Long id1 = findID(username1);
        Long id2 = findID(username2);
        Optional<FriendshipRequest> request = repoRequest.findOne(new Tuple<>(id1, id2));
        if (request.isEmpty()) {
            request = repoRequest.findOne(new Tuple<>(id2, id1));
        }
        if (request.isEmpty()) {
            throw new ServiceException("Nu există o cerere între acești utilizatori");
        }

        try {
            repoRequest.delete(request.get().getId());
            notifyAllObservers();
        } catch (Exception e) {
            throw new ServiceException("Eroare la ștergerea cererii: " + e.getMessage(), e);
        }
    }

    public void acceptRequest(String username1, String username2) {
        Long id1 = findID(username1);
        Long id2 = findID(username2);
        Optional<FriendshipRequest> request = repoRequest.findOne(new Tuple<>(id1, id2));
        if (request.isEmpty()) {
            request = repoRequest.findOne(new Tuple<>(id2, id1));
        }
        if (request.isEmpty()) {
            throw new ServiceException("Nu există o cerere între acești utilizatori");
        }

        try {
            Friendship friendship = new Friendship(new Tuple<>(id1, id2));
            friendship.setDate(LocalDateTime.now());
            repoFriendship.save(friendship);
            repoRequest.delete(request.get().getId());
            notifyAllObservers();
        } catch (Exception e) {
            throw new ServiceException("Eroare la acceptarea cererii: " + e.getMessage(), e);
        }
    }


    public Iterable<FriendshipRequest> getAllRequestss() {

        return repoRequest.findAll();
    }

    public Iterable<Tuple<User, FriendshipRequest>> getAllRequests(User user) {
        List<Tuple<User, FriendshipRequest>> requests = new ArrayList<>();

        for (FriendshipRequest f : repoRequest.findAll()) {
            if (f.getId().getRight().equals(user.getId())) {
                // Cerere trimisă de un alt utilizator către utilizatorul curent
                User sender = repoUser.findOne(f.getId().getLeft()).orElse(null);
                if (sender != null) {
                    requests.add(new Tuple<>(sender, f));
                }
            }
        }

        return requests;
    }

    public void updateRequestStatus(String username1, String username2, String newStatus) throws SQLException {
        FriendshipRequest friendshipRequest = new FriendshipRequest(new Tuple<>(findID(username1), findID(username2)));
       friendshipRequest.setStatus(newStatus);
        repoRequest.update(friendshipRequest);
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
}

