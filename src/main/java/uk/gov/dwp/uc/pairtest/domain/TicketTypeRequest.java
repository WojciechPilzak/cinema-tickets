package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable Object
 */

public record TicketTypeRequest(Type type, int noOfTickets) {

    public enum Type {
        ADULT(25),
        CHILD(15),
        INFANT(0);
        private final int price;

        Type(int price) {
            this.price = price;
        }

        public int getPrice() {
            return price;
        }
    }

}
