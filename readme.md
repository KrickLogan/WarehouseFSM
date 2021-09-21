# WarehouseFSM ReadMe

## Project Description
- A Finite State Machine with a Graphical User Interface for all user states. States include a Login, QueryClient, ShoppingCart, Customer, Clerk, and Manager state. The Customer, Clerk, and Manager contains access and functionality that pertains to the specific type of user.
  - Clients are granted access to the ShoppingCart state.
  - Clerks are granted access to the QueryClient, Client, and ShoppingCart states.
  - Managers are granted access to all states.


## Instructions for compiling and running WarehouseFSM
1. Navigate to the project directory.
2. Compile all java files using 'javac *.java'.
3. Run the project by using 'java WareContext'.
