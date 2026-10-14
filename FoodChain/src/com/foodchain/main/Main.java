
package com.foodchain.main;

import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static class User {
        String name, phone, email, password, role;

        User(String name, String phone, String email,
             String password, String role) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.password = password;
            this.role = role;
        }
    }

    static class Product {
        String name, code;
        int quantity;

        Product(String name, String code, int quantity) {
            this.name = name;
            this.code = code;
            this.quantity = quantity;
        }
    }

    static class SupplyRequest {
        String farmer, product;
        int quantity;
        String status = "Pending";

        SupplyRequest(String farmer, String product, int quantity) {
            this.farmer = farmer;
            this.product = product;
            this.quantity = quantity;
        }
    }

    static class CustomerOrder {
        String customer, product;
        int quantity;
        String status = "Confirmed";

        CustomerOrder(String customer, String product, int quantity) {
            this.customer = customer;
            this.product = product;
            this.quantity = quantity;
        }
    }

    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<Product> products = new ArrayList<>();
    static ArrayList<SupplyRequest> requests = new ArrayList<>();
    static ArrayList<CustomerOrder> orders = new ArrayList<>();
    static ArrayList<Product> inventory = new ArrayList<>();

    static int productCounter = 1001;

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n==================================");
            System.out.println("   FOODCHAIN MANAGEMENT SYSTEM");
            System.out.println("==================================");
            System.out.println("1. Farmer");
            System.out.println("2. Warehouse");
            System.out.println("3. Customer");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            int choice = readNumber();

            switch (choice) {
                case 1:
                    roleMenu("Farmer");
                    break;
                case 2:
                    roleMenu("Warehouse");
                    break;
                case 3:
                    roleMenu("Customer");
                    break;
                case 4:
                    System.out.println("Thank you for using FoodChain!");
                    return;
                default:
                    System.out.println("Invalid");
            }
        }
    }

    static int readNumber() {
        String input = sc.nextLine().trim();

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static String readText(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    static void roleMenu(String role) {
        while (true) {
            System.out.println("\n-------- " + role.toUpperCase()
                    + " MODULE --------");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Back");

            int choice = readNumber();

            if (choice == 1) {
                register(role);
            } else if (choice == 2) {
                User user = login(role);

                if (user != null) {
                    if (role.equals("Farmer")) {
                        farmerDashboard(user);
                    } else if (role.equals("Warehouse")) {
                        warehouseDashboard(user);
                    } else {
                        customerDashboard(user);
                    }
                }
            } else if (choice == 3) {
                return;
            } else {
                System.out.println("Invalid");
            }
        }
    }

    static void register(String role) {
        String name = readText("Enter name: ");
        String phone = readText("Enter 10-digit phone: ");
        String email = readText("Enter email: ");
        String password = readText("Enter password: ");

        if (name.isEmpty()
                || !phone.matches("[0-9]{10}")
                || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                || password.length() < 4) {
            System.out.println("Invalid");
            return;
        }

        for (User u : users) {
            if (u.email.equalsIgnoreCase(email)) {
                System.out.println("Invalid");
                return;
            }
        }

        users.add(new User(name, phone, email, password, role));
        System.out.println("Registration successful!");
    }

    static User login(String role) {
        String email = readText("Enter email: ");
        String password = readText("Enter password: ");

        for (User u : users) {
            if (u.role.equals(role)
                    && u.email.equalsIgnoreCase(email)
                    && u.password.equals(password)) {
                System.out.println("Login successful! Welcome "
                        + u.name);
                return u;
            }
        }

        System.out.println("Invalid");
        return null;
    }

    // FARMER MODULE

    static void farmerDashboard(User farmer) {
        while (true) {
            System.out.println("\n===== FARMER DASHBOARD =====");
            System.out.println("1. Add Product");
            System.out.println("2. View My Products");
            System.out.println("3. Search Product");
            System.out.println("4. Update Product");
            System.out.println("5. Remove Product");
            System.out.println("6. Send Supply Request");
            System.out.println("7. View My Requests");
            System.out.println("8. Logout");

            int choice = readNumber();

            switch (choice) {
                case 1:
                    addProduct(farmer);
                    break;
                case 2:
                    viewProducts(farmer);
                    break;
                case 3:
                    searchProduct(farmer);
                    break;
                case 4:
                    updateProduct(farmer);
                    break;
                case 5:
                    removeProduct(farmer);
                    break;
                case 6:
                    sendRequest(farmer);
                    break;
                case 7:
                    viewRequests(farmer);
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Invalid");
            }
        }
    }

    static void addProduct(User farmer) {
        String name = readText("Product name: ");
        String qtyText = readText("Quantity: ");

        int qty;
        try {
            qty = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            System.out.println("Invalid");
            return;
        }

        if (name.isEmpty() || qty <= 0) {
            System.out.println("Invalid");
            return;
        }

        String code = "FC" + productCounter++;
        products.add(new Product(name, code, qty));

        System.out.println("Product added successfully!");
        System.out.println("Product code: " + code);
    }

    static void viewProducts(User farmer) {
        boolean found = false;

        System.out.println("\n--- PRODUCT LIST ---");
        for (Product p : products) {
            System.out.println(p.code + " | " + p.name
                    + " | Quantity: " + p.quantity);
            found = true;
        }

        if (!found) {
            System.out.println("No products available.");
        }
    }

    static void searchProduct(User farmer) {
        String key = readText("Enter product name or code: ");
        boolean found = false;

        for (Product p : products) {
            if (p.name.equalsIgnoreCase(key)
                    || p.code.equalsIgnoreCase(key)) {
                System.out.println("Product: " + p.name
                        + " | Code: " + p.code
                        + " | Quantity: " + p.quantity);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Invalid");
        }
    }

    static void updateProduct(User farmer) {
        String code = readText("Enter product code: ");

        for (Product p : products) {
            if (p.code.equalsIgnoreCase(code)) {
                String name = readText("New product name: ");
                String qtyText = readText("New quantity: ");

                int qty;
                try {
                    qty = Integer.parseInt(qtyText);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid");
                    return;
                }

                if (name.isEmpty() || qty <= 0) {
                    System.out.println("Invalid");
                    return;
                }

                p.name = name;
                p.quantity = qty;
                System.out.println("Product updated successfully!");
                return;
            }
        }

        System.out.println("Invalid");
    }

    static void removeProduct(User farmer) {
        String code = readText("Enter product code: ");

        for (Product p : products) {
            if (p.code.equalsIgnoreCase(code)) {
                products.remove(p);
                System.out.println("Product removed successfully!");
                return;
            }
        }

        System.out.println("Invalid");
    }

    static void sendRequest(User farmer) {
        String code = readText("Enter product code: ");
        Product selected = null;

        for (Product p : products) {
            if (p.code.equalsIgnoreCase(code)) {
                selected = p;
                break;
            }
        }

        if (selected == null) {
            System.out.println("Invalid");
            return;
        }

        String qtyText = readText("Supply quantity: ");
        int qty;

        try {
            qty = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            System.out.println("Invalid");
            return;
        }

        if (qty <= 0 || qty > selected.quantity) {
            System.out.println("Invalid");
            return;
        }

        requests.add(new SupplyRequest(
                farmer.email, selected.code, qty));

        selected.quantity -= qty;

        System.out.println("Supply request submitted successfully!");
    }

    static void viewRequests(User farmer) {
        boolean found = false;

        for (SupplyRequest r : requests) {
            if (r.farmer.equals(farmer.email)) {
                System.out.println("Product code: " + r.product
                        + " | Quantity: " + r.quantity
                        + " | Status: " + r.status);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No supply requests found.");
        }
    }

    // WAREHOUSE MODULE

    static void warehouseDashboard(User warehouse) {
        while (true) {
            System.out.println("\n===== WAREHOUSE DASHBOARD =====");
            System.out.println("1. View Supply Requests");
            System.out.println("2. Approve Supply Request");
            System.out.println("3. Reject Supply Request");
            System.out.println("4. View Inventory");
            System.out.println("5. View Customer Orders");
            System.out.println("6. Update Order Status");
            System.out.println("7. Logout");

            int choice = readNumber();

            switch (choice) {
                case 1:
                    viewAllRequests();
                    break;
                case 2:
                    processRequest(true);
                    break;
                case 3:
                    processRequest(false);
                    break;
                case 4:
                    viewInventory();
                    break;
                case 5:
                    viewOrders();
                    break;
                case 6:
                    updateOrderStatus();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid");
            }
        }
    }

    static void viewAllRequests() {
        if (requests.isEmpty()) {
            System.out.println("No supply requests available.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            SupplyRequest r = requests.get(i);
            System.out.println((i + 1) + ". Product: " + r.product
                    + " | Quantity: " + r.quantity
                    + " | Status: " + r.status);
        }
    }

    static void processRequest(boolean approve) {
        viewAllRequests();

        if (requests.isEmpty()) {
            return;
        }

        String input = readText("Enter request number: ");
        int number;

        try {
            number = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid");
            return;
        }

        if (number < 1 || number > requests.size()) {
            System.out.println("Invalid");
            return;
        }

        SupplyRequest r = requests.get(number - 1);

        if (!r.status.equals("Pending")) {
            System.out.println("Invalid");
            return;
        }

        if (approve) {
            Product original = null;

            for (Product p : products) {
                if (p.code.equalsIgnoreCase(r.product)) {
                    original = p;
                    break;
                }
            }

            if (original == null) {
                System.out.println("Invalid");
                return;
            }

            Product stock = null;
            for (Product p : inventory) {
                if (p.code.equalsIgnoreCase(original.code)) {
                    stock = p;
                    break;
                }
            }

            if (stock == null) {
                inventory.add(new Product(
                        original.name, original.code, r.quantity));
            } else {
                stock.quantity += r.quantity;
            }

            r.status = "Approved";
            System.out.println("Supply request approved!");
        } else {
            r.status = "Rejected";

            for (Product p : products) {
                if (p.code.equalsIgnoreCase(r.product)) {
                    p.quantity += r.quantity;
                    break;
                }
            }

            System.out.println("Supply request rejected!");
        }
    }

    static void viewInventory() {
        System.out.println("\n--- WAREHOUSE INVENTORY ---");

        if (inventory.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }

        for (Product p : inventory) {
            System.out.println(p.code + " | " + p.name
                    + " | Available: " + p.quantity);
        }
    }

    static void viewOrders() {
        if (orders.isEmpty()) {
            System.out.println("No customer orders available.");
            return;
        }

        for (int i = 0; i < orders.size(); i++) {
            CustomerOrder o = orders.get(i);
            System.out.println((i + 1) + ". " + o.product
                    + " | Quantity: " + o.quantity
                    + " | Status: " + o.status);
        }
    }

    static void updateOrderStatus() {
        viewOrders();

        if (orders.isEmpty()) {
            return;
        }

        String input = readText("Enter order number: ");
        int number;

        try {
            number = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid");
            return;
        }

        if (number < 1 || number > orders.size()) {
            System.out.println("Invalid");
            return;
        }

        CustomerOrder o = orders.get(number - 1);

        if (o.status.equals("Confirmed")) {
            o.status = "Dispatched";
        } else if (o.status.equals("Dispatched")) {
            o.status = "Delivered";
        } else {
            System.out.println("Invalid");
            return;
        }

        System.out.println("Order status: " + o.status);
    }

    // CUSTOMER MODULE

    static void customerDashboard(User customer) {
        while (true) {
            System.out.println("\n===== CUSTOMER DASHBOARD =====");
            System.out.println("1. Browse Products");
            System.out.println("2. Search Product");
            System.out.println("3. Place Order");
            System.out.println("4. My Orders");
            System.out.println("5. Track Order");
            System.out.println("6. Logout");

            int choice = readNumber();

            switch (choice) {
                case 1:
                    browseProducts();
                    break;
                case 2:
                    searchInventory();
                    break;
                case 3:
                    placeOrder(customer);
                    break;
                case 4:
                    myOrders(customer);
                    break;
                case 5:
                    trackOrder(customer);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid");
            }
        }
    }

    static void browseProducts() {
        viewInventory();
    }

    static void searchInventory() {
        String key = readText("Enter product name: ");
        boolean found = false;

        for (Product p : inventory) {
            if (p.name.toLowerCase().contains(key.toLowerCase())
                    && p.quantity > 0) {
                System.out.println(p.name + " | Code: " + p.code
                        + " | Available: " + p.quantity);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Invalid");
        }
    }

    static void placeOrder(User customer) {
        String code = readText("Enter product code: ");
        Product selected = null;

        for (Product p : inventory) {
            if (p.code.equalsIgnoreCase(code)) {
                selected = p;
                break;
            }
        }

        if (selected == null) {
            System.out.println("Invalid");
            return;
        }

        String qtyText = readText("Enter quantity: ");
        int qty;

        try {
            qty = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            System.out.println("Invalid");
            return;
        }

        if (qty <= 0 || qty > selected.quantity) {
            System.out.println("Invalid");
            return;
        }

        selected.quantity -= qty;

        orders.add(new CustomerOrder(
                customer.email, selected.code, qty));

        System.out.println("Order placed successfully!");
    }

    static void myOrders(User customer) {
        boolean found = false;

        for (int i = 0; i < orders.size(); i++) {
            CustomerOrder o = orders.get(i);

            if (o.customer.equals(customer.email)) {
                System.out.println("Order number: " + (i + 1)
                        + " | Product: " + o.product
                        + " | Quantity: " + o.quantity
                        + " | Status: " + o.status);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No orders found.");
        }
    }

    static void trackOrder(User customer) {
        String input = readText("Enter order number: ");
        int number;

        try {
            number = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid");
            return;
        }

        if (number < 1 || number > orders.size()) {
            System.out.println("Invalid");
            return;
        }

        CustomerOrder o = orders.get(number - 1);

        if (!o.customer.equals(customer.email)) {
            System.out.println("Invalid");
            return;
        }

        System.out.println("Shipment status: " + o.status);
    }
}
