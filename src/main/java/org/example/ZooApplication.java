package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//////////////////////////////////////////////
// 1. Core Interfaces и Абстрактные классы
//////////////////////////////////////////////

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
    private int kindness; // от 1 до 10

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

//////////////////////////////////////////////
// 2. Конкретные классы животных
//////////////////////////////////////////////

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

//////////////////////////////////////////////
// 3. Классы для инвентаризации
//////////////////////////////////////////////

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

//////////////////////////////////////////////
// 4. Сервис проверки здоровья
//////////////////////////////////////////////

interface VeterinaryClinic {
    boolean checkAnimal(Animal animal);
}

@Service
class VeterinaryClinicImpl implements VeterinaryClinic {
    @Override
    public boolean checkAnimal(Animal animal) {
        // Простая проверка: если животное потребляет > 0 кг еды, оно считается здоровым.
        return animal.getFood() > 0;
    }
}

//////////////////////////////////////////////
// 5. Интерфейсы для менеджеров и сервисов (DIP и ISP)
//////////////////////////////////////////////

interface IAnimalManager {
    boolean addAnimal(Animal animal);
    List<Animal> getAnimals();
    int getTotalFoodConsumption();
    List<Animal> getAnimalsEligibleForContactZoo();
}

interface IInventoryManager {
    void addInventoryItem(IInventory item);
    List<IInventory> getInventoryItems();
}

interface IReportService {
    void printAnimalReport();
    void printInventory();
    void printEligibleAnimalsReport();
}

//////////////////////////////////////////////
// 6. Реализация менеджеров и сервисов
//////////////////////////////////////////////

@Component
class AnimalManager implements IAnimalManager {
    private final List<Animal> animals = new ArrayList<>();

    @Autowired
    private VeterinaryClinic veterinaryClinic;

    @Override
    public boolean addAnimal(Animal animal) {
        if (veterinaryClinic.checkAnimal(animal)) {
            animals.add(animal);
            return true;
        }
        return false;
    }

    @Override
    public List<Animal> getAnimals() {
        return animals;
    }

    @Override
    public int getTotalFoodConsumption() {
        return animals.stream().mapToInt(Animal::getFood).sum();
    }

    @Override
    public List<Animal> getAnimalsEligibleForContactZoo() {
        List<Animal> eligible = new ArrayList<>();
        for (Animal a : animals) {
            if (a.isEligibleForContactZoo()) {
                eligible.add(a);
            }
        }
        return eligible;
    }
}

@Component
class InventoryManager implements IInventoryManager {
    private final List<IInventory> inventoryItems = new ArrayList<>();

    @Override
    public void addInventoryItem(IInventory item) {
        inventoryItems.add(item);
    }

    @Override
    public List<IInventory> getInventoryItems() {
        return inventoryItems;
    }
}

@Component
class ReportService implements IReportService {

    @Autowired
    private IAnimalManager animalManager;

    @Autowired
    private IInventoryManager inventoryManager;

    @Override
    public void printAnimalReport() {
        System.out.println("Общее количество животных: " + animalManager.getAnimals().size());
        System.out.println("Общее количество килограммов еды в день: " +
                animalManager.getTotalFoodConsumption() + " кг");
    }

    @Override
    public void printInventory() {
        System.out.println("Инвентаризация (животные и вещи):");
        for (IInventory item : inventoryManager.getInventoryItems()) {
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

    @Override
    public void printEligibleAnimalsReport() {
        List<Animal> eligible = animalManager.getAnimalsEligibleForContactZoo();
        System.out.println("Животные, пригодные для контактного зоопарка:");
        for (Animal a : eligible) {
            System.out.println(a.getName() + " (Номер: " + a.getNumber() + ")");
        }
    }
}

//////////////////////////////////////////////
// 7. Консольный интерфейс (ConsoleUI)
//////////////////////////////////////////////

@Component
class ConsoleUI {

    @Autowired
    private IAnimalManager animalManager;

    @Autowired
    private IInventoryManager inventoryManager;

    @Autowired
    private IReportService reportService;

    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n--- Меню Зоопарка ---");
            System.out.println("1. Добавить животное");
            System.out.println("2. Вывести отчет по животным");
            System.out.println("3. Показать животных для контактного зоопарка");
            System.out.println("4. Вывести инвентаризацию");
            System.out.println("5. Выход");
            System.out.print("Выберите действие: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addAnimal();
                    break;
                case "2":
                    reportService.printAnimalReport();
                    break;
                case "3":
                    reportService.printEligibleAnimalsReport();
                    break;
                case "4":
                    reportService.printInventory();
                    break;
                case "5":
                    System.out.println("Выход из программы.");
                    return;
                default:
                    System.out.println("Некорректный выбор. Попробуйте снова.");
            }
        }
    }

    private void addAnimal() {
        System.out.print("Введите тип животного (Monkey, Rabbit, Tiger, Wolf): ");
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
            return;
        }

        if (animalManager.addAnimal(animal)) {
            System.out.println("Животное " + animal.getName() + " принято в зоопарк.");
            inventoryManager.addInventoryItem(animal);
        } else {
            System.out.println("Животное " + animal.getName() + " не прошло проверку здоровья.");
        }
    }
}

//////////////////////////////////////////////
// 8. Главный класс приложения
//////////////////////////////////////////////

@Configuration
@ComponentScan(basePackages = "org.example")
public class ZooApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ZooApplication.class);

        IInventoryManager inventoryManager = context.getBean(IInventoryManager.class);
        inventoryManager.addInventoryItem(context.getBean(Table.class));
        inventoryManager.addInventoryItem(context.getBean(Computer.class));

        ConsoleUI consoleUI = context.getBean(ConsoleUI.class);
        consoleUI.run();

        context.close();
    }
}
