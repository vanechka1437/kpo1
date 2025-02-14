package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface IAlive {
    int getFood();
    void setFood(int food);
}

interface IInventory {
    int getNumber();
    void setNumber(int number);
}

abstract class Animal implements IAlive, IInventory {
    private final String name;
    private int food;
    private int number;

    public Animal(String name, int food, int number) {
        this.name = name;
        this.food = food;
        this.number = number;
    }

    public String getName() { return name; }
    public int getFood() { return food; }
    public void setFood(int food) { this.food = food; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public abstract boolean isEligibleForContactZoo();
}

abstract class Herbo extends Animal {
    private int kindness; // уровень доброты от 1 до 10

    public Herbo(String name, int food, int number, int kindness) {
        super(name, food, number);
        this.kindness = kindness;
    }
    public int getKindness() { return kindness; }
    public void setKindness(int kindness) { this.kindness = kindness; }

    @Override
    public boolean isEligibleForContactZoo() {
        return kindness > 5;
    }
}

abstract class Predator extends Animal {
    public Predator(String name, int food, int number) {
        super(name, food, number);
    }
    @Override
    public boolean isEligibleForContactZoo() {
        return false;
    }
}

@Component
class Monkey extends Herbo {
    public Monkey() {
        super("Monkey", 5, 1001, 7);
    }
}

@Component
class Rabbit extends Herbo {
    public Rabbit() {
        super("Rabbit", 2, 1002, 8);
    }
}

@Component
class Tiger extends Predator {
    public Tiger() {
        super("Tiger", 10, 2001);
    }
}

@Component
class Wolf extends Predator {
    public Wolf() {
        super("Wolf", 8, 2002);
    }
}

class Thing implements IInventory {
    private final String name;
    private int number;

    public Thing(String name, int number) {
        this.name = name;
        this.number = number;
    }
    public String getName() { return name; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
}

@Component
class Table extends Thing {
    public Table() {
        super("Table", 3001);
    }
}

@Component
class Computer extends Thing {
    public Computer() {
        super("Computer", 3002);
    }
}

interface VeterinaryClinic {
    boolean checkAnimal(Animal animal);
}

@Service
class VeterinaryClinicImpl implements VeterinaryClinic {
    @Override
    public boolean checkAnimal(Animal animal) {
        // Простейшая проверка: если количество потребляемой еды > 0, считаем животное здоровым.
        return animal.getFood() > 0;
    }
}

@Component
class Zoo {
    private final List<Animal> animals = new ArrayList<>();
    private final List<IInventory> inventoryItems = new ArrayList<>();

    @Autowired
    private VeterinaryClinic vetClinic;

    public void addAnimal(Animal animal) {
        if (vetClinic.checkAnimal(animal)) {
            animals.add(animal);
            System.out.println("Животное " + animal.getName() + " принято в зоопарк.");
        } else {
            System.out.println("Животное " + animal.getName() + " не прошло проверку здоровья.");
        }
    }

    public void addInventoryItem(IInventory item) {
        inventoryItems.add(item);
    }

    public int getTotalFoodConsumption() {
        return animals.stream().mapToInt(Animal::getFood).sum();
    }

    public List<Animal> getAnimalsEligibleForContactZoo() {
        List<Animal> eligible = new ArrayList<>();
        for (Animal a : animals) {
            if (a.isEligibleForContactZoo()) {
                eligible.add(a);
            }
        }
        return eligible;
    }

    public void printInventory() {
        System.out.println("Инвентаризация (животные и вещи):");
        for (IInventory item : inventoryItems) {
            String itemName;
            if (item instanceof Animal) {
                itemName = ((Animal) item).getName();
            } else if (item instanceof Thing) {
                itemName = ((Thing) item).getName();
            } else {
                itemName = "Неизвестный объект";
            }
            System.out.println("Наименование: " + itemName + ", Номер: " + item.getNumber());
        }
    }

    public void printAnimalReport() {
        System.out.println("Общее количество животных: " + animals.size());
        System.out.println("Общее количество килограммов еды в день: " + getTotalFoodConsumption() + " кг");
    }
}

class MonkeyCustom extends Herbo {
    public MonkeyCustom(String name, int food, int number, int kindness) {
        super(name, food, number, kindness);
    }
}

class RabbitCustom extends Herbo {
    public RabbitCustom(String name, int food, int number, int kindness) {
        super(name, food, number, kindness);
    }
}

class TigerCustom extends Predator {
    public TigerCustom(String name, int food, int number) {
        super(name, food, number);
    }
}

class WolfCustom extends Predator {
    public WolfCustom(String name, int food, int number) {
        super(name, food, number);
    }
}

@Configuration
@ComponentScan(basePackages = "org.example")
public class ZooApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ZooApplication.class);
        Zoo zoo = context.getBean(Zoo.class);

        zoo.addInventoryItem(context.getBean(Table.class));
        zoo.addInventoryItem(context.getBean(Computer.class));

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Меню Зоопарка ---");
            System.out.println("1. Добавить животное");
            System.out.println("2. Вывести отчет по животным");
            System.out.println("3. Показать животных для контактного зоопарка");
            System.out.println("4. Вывести инвентаризацию");
            System.out.println("5. Выход");
            System.out.print("Выберите действие (введите цифру): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Введите тип животного (выберите из: Monkey, Rabbit, Tiger, Wolf): ");
                    String type = scanner.nextLine().trim();
                    System.out.print("Введите имя: ");
                    String name = scanner.nextLine();
                    System.out.print("Введите количество потребляемой еды (кг/день): ");
                    int food = Integer.parseInt(scanner.nextLine());
                    System.out.print("Введите инвентаризационный номер: ");
                    int number = Integer.parseInt(scanner.nextLine());

                    Animal animal = null;
                    if ("Monkey".equalsIgnoreCase(type)) {
                        System.out.print("Введите уровень доброты (1-10): ");
                        int kindness = Integer.parseInt(scanner.nextLine());
                        animal = new MonkeyCustom(name, food, number, kindness);
                    } else if ("Rabbit".equalsIgnoreCase(type)) {
                        System.out.print("Введите уровень доброты (1-10): ");
                        int kindness = Integer.parseInt(scanner.nextLine());
                        animal = new RabbitCustom(name, food, number, kindness);
                    } else if ("Tiger".equalsIgnoreCase(type)) {
                        animal = new TigerCustom(name, food, number);
                    } else if ("Wolf".equalsIgnoreCase(type)) {
                        animal = new WolfCustom(name, food, number);
                    } else {
                        System.out.println("Неизвестный тип животного.");
                        break;
                    }
                    zoo.addAnimal(animal);
                    zoo.addInventoryItem(animal);
                    break;
                case "2":
                    zoo.printAnimalReport();
                    break;
                case "3":
                    List<Animal> eligible = zoo.getAnimalsEligibleForContactZoo();
                    System.out.println("Животные, пригодные для контактного зоопарка:");
                    for (Animal a : eligible) {
                        System.out.println(a.getName() + " (Номер: " + a.getNumber() + ")");
                    }
                    break;
                case "4":
                    zoo.printInventory();
                    break;
                case "5":
                    System.out.println("Выход из программы.");
                    context.close();
                    System.exit(0);
                default:
                    System.out.println("Некорректный выбор. Попробуйте снова.");
            }
        }
    }
}
