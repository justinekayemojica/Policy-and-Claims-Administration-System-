# Policy-and-Claims-Administration-System-

PAS System Menu Options

1-Create a new Customer Account
2-Get a policy quote and buy the policy.
3-Cancel a specific policy (i.e change the expiration date of a policy to an earlier date than originally specified) 
4-File an accident claim against a policy. All claims must be maintained by system and should be searchable.
5-Search for a Customer account 
6-Search for and display a specific policy
7-Search for and display a specific claim
8-Exit the PAS System

The PAS System top-level algorithm:

Display a menu of options.
Prompt the user for a menu choice.
For option #2, prompt the user for the account number of the account containing the relevant policy – a customer account must exist before policy purchase. For option #3, #4, and #6, prompt the user for the specific policy number. For option #5, prompt the user for the first name and last name of the customer to search for (For now we will assume customer names are unique). For option #7, prompt the user for the specific claim number needed.
Once the user selects an option, perform the action requested by the user on the identified account or policy.
Repeat steps 1 through 7 until the user has selected the option to exit.
