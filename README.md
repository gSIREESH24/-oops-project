YOU NEED TO RUN OPENPAGE(MAIN PAGE),(in all others also the main is there it is because we change modification fast);
1. User Functionality:
	•	UserLoginPage
	•	Handles user login and signup.
	•	Connects to the database and creates user accounts.
	•	HomePage
	•	Displays the homepage after login.
	•	Uses LoadUserIDFromName() and LoadImageFromDatabase() to personalize content.
	•	SearchEngine
	•	Allows users to search products by keyword.
	•	Inserts selected products into the user’s cart.
	•	ProductDetails
	•	Helps insert selected product data into the user’s cart using product details like name, price, description, etc.
	•	Cart
	•	Manages items in the user’s cart.
	•	Handles operations: load cart data, update quantity, delete items, show bill.
	•	UserProfile
	•	Displays user profile and their orders.
	•	Has methods for profile and order management.
	•	CreateProfile & UpdateProfile
	•	Create and update user profile information, including phone number verification.

⸻

2. Vendor Functionality:
	•	VendorLogin
	•	Handles vendor login and signup.
	•	Vendor account creation includes shop and product details.
	•	VendorHomePage
	•	Loads vendor-related data from the database.
	•	VendorProfile
	•	Loads and displays vendor-specific profile data.
	•	AddProducts
	•	Allows vendors to insert new products into the system database.

⸻

General Flow:
	•	Users: Login → Search → View Product → Add to Cart → Checkout → View Orders.
	•	Vendors: Login → Add Products → Manage Profile.

⸻

Database Integration:
	•	All components interact with the database for CRUD operations like:
	•	Creating accounts and profiles.
	•	Storing product, cart, and order data.
	•	Loading user/vendor-specific data.
