# Cinema Tickets Service

## Overview
This project is an implementation of the **Cinema Tickets Service** coding exercise for the Department for Work and Pensions (DWP).  
It contains an implementation of the `TicketService` interface along with supporting classes and unit tests.

The solution has been developed using **Java 21** and **Maven**.

---

## Business Rules Implemented
- Three ticket types are supported:
    - **ADULT** – £25
    - **CHILD** – £15
    - **INFANT** – £0 (no seat allocated)
- A maximum of **25 tickets** can be purchased per request.
- **CHILD** and **INFANT** tickets require at least one **ADULT** ticket in the order.
- A valid `accountId` (greater than zero) is required.
- Payments are processed through the provided `TicketPaymentService`.
- Seats are reserved through the provided `SeatReservationService`.
- Invalid purchase requests result in an `InvalidPurchaseException`.

---

## Build and Run
The project uses Maven. To build and run tests:

```bash
mvn clean test
```

## Requirements

- Java 21
- Maven 3.9+

## Notes

- The TicketService interface and the thirdparty.* packages must not be modified.
- TicketTypeRequest is immutable.
- All invalid requests are rejected with InvalidPurchaseException.

