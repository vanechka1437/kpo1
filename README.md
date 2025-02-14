#### 1. Single responsibility principle 

Каждый класс имеет одну четко определенную ответственность:
- AnimalManager: Управляет списком животных – добавление, вычисление общего потребления еды, отбор животных для контактного зоопарка.
- InventoryManager: Отвечает за учет всех инвентарных объектов.
- ReportService: Формирует отчёты, используя данные из менеджеров.
- ConsoleUI: Обеспечивает взаимодействие с пользователем через консольное меню, делегируя задачи соответствующим сервисам.

#### 2. Open-Closed principle

- Базовые классы и интерфейсы для животных: Абстрактный класс Animal и его наследники Herbo и Predator позволяют добавлять новые типы животных без изменения логики уже реализованных методов.
- Интерфейсы менеджеров и сервисов: Интерфейсы IAnimalManager, IInventoryManager и IReportService дают возможность создавать новые реализации без изменения кода, зависящего от этих интерфейсов.

#### 3. Liskov Substitution Principle

- Любой наследник базового класса Animal может использоваться там, где ожидается объект типа Animal

#### 4. Interface Segregation Principle

Клиенты зависят только от тех методов, которые им действительно необходимы:
- IAlive и IInventory: Интерфейсы разделяют обязанности – один отвечает за учет потребления еды, другой – за инвентаризационные данные.
- Интерфейсы менеджеров (например, IAnimalManager) содержат только те методы, которые требуются для работы с животными, исключая лишнюю функциональность.

#### 5. Dependency Inversion Principle 

Модули высокого уровня зависят от абстракций, а не от конкретных реализаций:

- AnimalManager зависит от абстракции VeterinaryClinic для проверки состояния животных.
- ConsoleUI и ReportService получают зависимости через интерфейсы.
