package com.example.map_toysocialnetwork;

import com.example.map_toysocialnetwork.domain.Friendship;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.service.Service;
import com.example.map_toysocialnetwork.service.ServiceException;

import java.util.Scanner;

/*
public class UI {
    private Service service;

    public UI(Service service) {
        this.service = service;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Adaugă User");
            System.out.println("2. Elimină User");
            System.out.println("3. Adaugă Friendship");
            System.out.println("4. Elimină Friendship");
            System.out.println("5. Afisare nr de comunitati");
            System.out.println("6. Cea mai sociabila comunitate");
            System.out.println("7. Afiseaza toti Userii");
            System.out.println("8. Afiseaza toata prieteniile");
            System.out.println("9. Acutualizeaza user");
            System.out.println("10. Ieșire");
            System.out.print("Alege o opțiune: ");

            int optiune = scanner.nextInt();
            try {
                if (optiune == 1) {
                    //  System.out.print("Introdu ID-ul Userului: ");
                    //  Long id = scanner.nextLong();
                    System.out.print("Introdu prenumele Userului: ");
                    String firstName = scanner.next();
                    System.out.print("Introdu numele de familie al Userului: ");
                    String lastName = scanner.next();
                    User user = new User(firstName, lastName);
                    //   user.setId(id);
                    service.addUser(user);
                    System.out.println("User adăugat cu succes!");
                } else if (optiune == 2) {
                    // Remove User
                    System.out.print("Introdu ID-ul Userului de eliminat: ");
                    Long removeId = scanner.nextLong();
                    service.removeUser(removeId);
                    System.out.println("User eliminat cu succes!");
                } else if (optiune == 3) {
                    System.out.print("Introdu ID-ul primului User: ");
                    Long id1 = scanner.nextLong();
                    System.out.print("Introdu ID-ul celui de-al doilea User: ");
                    Long id2 = scanner.nextLong();
                    service.addFriendship(id1, id2);
                    System.out.println("Prietenie adăugată cu succes!");
                } else if (optiune == 4) {
                    System.out.print("Introdu ID-ul primului User: ");
                    Long id1 = scanner.nextLong();
                    System.out.print("Introdu ID-ul celui de-al doilea User: ");
                    Long id2 = scanner.nextLong();
                    service.removeFriendship(id1, id2);
                    System.out.println("Prietenie eliminată cu succes!");
                } else if (optiune == 5) {
                    int nrComunitati = service.nrCommunities();
                    System.out.println("Numărul de comunități: " + nrComunitati);
                } else if (optiune == 6) {
                    var sociableCommunities = service.mostSociableCommunity();
                    System.out.println("Cea mai sociabilă comunitate:");
                    for (Iterable<User> community : sociableCommunities) {
                        for (User user : community) {
                            System.out.println(user);
                        }
                        System.out.println("-----");
                    }
                } else if (optiune == 7) {
                    System.out.println("Toți Userii:");
                    for (User user : service.getAllUsers()) {
                        System.out.println(user);
                    }
                } else if (optiune == 8) {
                    System.out.println("Toate prieteniile:");
                    for (Friendship friendship : service.getAllFriendships()) {
                        System.out.println(friendship);
                    }
                } else if (optiune == 9) {
                    //  System.out.print("Introdu ID-ul Userului: ");
                    //  Long id = scanner.nextLong();
                    System.out.print("Introdu ID-ul Userului pe care vrei sa il actualizezi: ");
                    Long updateId = scanner.nextLong();
                    System.out.print("Introdu noul prenume: ");
                    String firstName = scanner.next();
                    System.out.print("Introdu noul nume: ");
                    String lastName = scanner.next();
                    User user = new User(firstName, lastName);
                    //   user.setId(id);
                    service.updateUser(updateId, user);
                    System.out.println("User actualizat cu succes!");
                }  else if (optiune == 10) {
                    break;
                } else {
                    System.out.println("Opțiune invalidă. Vă rugăm să încercați din nou.");
                }
            } catch (ServiceException e) {
                System.out.println("Eroare: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("A apărut o eroare neașteptată: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
 */
