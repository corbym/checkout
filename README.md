# Checkout Kata
Assumptions: 

* Each transaction the user requires should create a new CheckoutSystem object rather than reuse as a singleton.
* Transactions were not modelled, althought they were mentioned in the specification. This is up to the user to implement.
* Items are scanned one at a time.
* No validation required for the pricing rules - e.g. prices can be negative, discounts can actually increase the price if required.
* If discounts are given with triggering amounts less than or equal to zero, the buisiness wants them applied anyway. Normally I would consult with the BA.
* The tests written are both unit and acceptance tests in the same.
* Only one PriceRule per SKU is ever specified. Rules specified later override rules specified earlier.

# Future

* Implement applying multiple PriceRules (3 for 2, 5 for £5 etc). This was not in the specification.
* Consider encapsulation of the calculation methods into delegates.
