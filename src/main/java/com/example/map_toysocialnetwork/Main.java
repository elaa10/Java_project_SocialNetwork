
package com.example.map_toysocialnetwork;

public class Main{
    public static void main(String[] args) {
        HelloApplication.launch(HelloApplication.class, args);
    }
}





/*
package com.example.map_toysocialnetwork;

import com.example.map_toysocialnetwork.domain.Friendship;
import com.example.map_toysocialnetwork.domain.Tuple;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.domain.validators.FriendshipValidator;
import com.example.map_toysocialnetwork.domain.validators.UserValidator;
import com.example.map_toysocialnetwork.domain.validators.ValidationException;
import com.example.map_toysocialnetwork.repository.Repository;
import com.example.map_toysocialnetwork.repository.dataBase.FriendshipDataBaseRepository;
import com.example.map_toysocialnetwork.repository.dataBase.UserDataBaseRepository;
import com.example.map_toysocialnetwork.repository.file.FriendshipRepository;
import com.example.map_toysocialnetwork.repository.file.UserRepository;
import com.example.map_toysocialnetwork.repository.memory.InMemoryRepository;
import com.example.map_toysocialnetwork.service.Service;
import com.example.map_toysocialnetwork.UI;


// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        // Repository<Long, User> repoUser = new UserRepository(new UserValidator(), "./data/users.txt");
        // Repository<Tuple<Long, Long>, Friendship> repoFriendship = new FriendshipRepository(new FriendshipValidator(), "./data/prietenii.txt");

        com.example.map_toysocialnetwork.repository.dataBase.UserDataBaseRepository userDataBaseRepo = new com.example.map_toysocialnetwork.repository.dataBase.UserDataBaseRepository("jdbc:postgresql://localhost:5432/postgres", "postgres", "1005", new com.example.map_toysocialnetwork.domain.validators.UserValidator());
        com.example.map_toysocialnetwork.repository.dataBase.FriendshipDataBaseRepository friendshipDataBaseRepository = new com.example.map_toysocialnetwork.repository.dataBase.FriendshipDataBaseRepository("jdbc:postgresql://localhost:5432/postgres", "postgres", "1005", new com.example.map_toysocialnetwork.domain.validators.FriendshipValidator());

        com.example.map_toysocialnetwork.service.Service service = new com.example.map_toysocialnetwork.service.Service(userDataBaseRepo, friendshipDataBaseRepository);


        com.example.map_toysocialnetwork.UI userInterface = new com.example.map_toysocialnetwork.UI(service);
        userInterface.run();

    }

}
 */
