package edu.cinema.pricing;

import java.time.DayOfWeek;
import java.util.List;

public class PricingEngine {
    
    private static final double ADULT_PRICE = 10.00;
    private static final double CHILD_PRICE = 6.00;
    private static final double SENIOR_PRICE = 7.50;
    private static final double STUDENT_PRICE = 8.00;
    
    private static final double WEDNESDAY_DISCOUNT_RATE = 0.20;
    private static final double THREE_D_SURCHARGE = 2.00;
    private static final double GROUP_DISCOUNT_RATE = 0.10;
    private static final int GROUP_THRESHOLD = 4;
    
    public double basePrice(TicketType type) {
            if (type == null) {
                throw new IllegalArgumentException("Ticket type cannot be null");
            }
            
            return switch (type) {
                case ADULT -> ADULT_PRICE;
                case CHILD -> CHILD_PRICE;
                case SENIOR -> SENIOR_PRICE;
                case STUDENT -> STUDENT_PRICE;
                default -> throw new IllegalArgumentException("Unknown ticket type: " + type);
            };
        }
    
    public PriceBreakdown computeTotal(List<TicketType> tickets, DayOfWeek day, boolean is3D) {
        // Preconditions
        if (tickets == null) {
            throw new IllegalArgumentException("Tickets list cannot be null");
        }
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null");
        }
        
        double subtotal = 0.0;
        for (TicketType ticket : tickets) {
            subtotal += basePrice(ticket);
        }
        
        double currentTotal = subtotal;
        double wednesdayDisc = 0.0;
        double threeDSurcharge = 0.0;
        double groupDisc = 0.0;
        
        if (day == DayOfWeek.WEDNESDAY) {
            wednesdayDisc = currentTotal * WEDNESDAY_DISCOUNT_RATE;
            currentTotal -= wednesdayDisc;
        }
        
        if (is3D) {
            threeDSurcharge = THREE_D_SURCHARGE * tickets.size();
            currentTotal += threeDSurcharge;
        }
        
        if (tickets.size() >= GROUP_THRESHOLD) {
            groupDisc = currentTotal * GROUP_DISCOUNT_RATE;
            currentTotal -= groupDisc;
        }
        
        double total = Math.round(currentTotal * 100.0) / 100.0;
        
        return new PriceBreakdown(subtotal, wednesdayDisc, threeDSurcharge, groupDisc, total);
    }
}