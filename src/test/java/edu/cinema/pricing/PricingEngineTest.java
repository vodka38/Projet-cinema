package edu.cinema.pricing;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class PricingEngineTest {
    
    private PricingEngine engine;
    
    @BeforeEach
    void setUp() {
        engine = new PricingEngine();
    }
    
    @Test
    @DisplayName("Le prix de base pour un adulte est de 10.00")
    void basePrice_adult_shouldReturn10() {
        assertEquals(10.00, engine.basePrice(TicketType.ADULT), 0.001);
    }
    
    @Test
    @DisplayName("Le prix de base pour un enfant est de 6.00")
    void basePrice_child_shouldReturn6() {
        assertEquals(6.00, engine.basePrice(TicketType.CHILD), 0.001);
    }
    
    @Test
    @DisplayName("Le prix de base pour un senior est de 7.50")
    void basePrice_senior_shouldReturn7_50() {
        assertEquals(7.50, engine.basePrice(TicketType.SENIOR), 0.001);
    }
    
    @Test
    @DisplayName("Le prix de base pour un étudiant est de 8.00")
    void basePrice_student_shouldReturn8() {
        assertEquals(8.00, engine.basePrice(TicketType.STUDENT), 0.001);
    }
    
    @Test
    @DisplayName("Le prix de base avec un type null doit lancer une IllegalArgumentException")
    void basePrice_null_shouldThrow() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> engine.basePrice(null)
        );
        assertTrue(exception.getMessage().contains("null"));
    }
    
    
    @Test
    @DisplayName("Panier vide doit retourner un total de 0 pas une erreur")
    void computeTotal_emptyCart_shouldReturnZero() {
        List<TicketType> tickets = Collections.emptyList();
        PriceBreakdown breakdown = engine.computeTotal(tickets, DayOfWeek.MONDAY, false);
        
        assertEquals(0.0, breakdown.getSubtotal(), 0.001);
        assertEquals(0.0, breakdown.getWednesdayDisc(), 0.001);
        assertEquals(0.0, breakdown.getThreeDSurcharge(), 0.001);
        assertEquals(0.0, breakdown.getGroupDisc(), 0.001);
        assertEquals(0.0, breakdown.getTotal(), 0.001);
    }

    
    @ParameterizedTest
    @CsvSource({
        "ADULT, 10.00",
        "CHILD, 6.00",
        "SENIOR, 7.50",
        "STUDENT, 8.00"
    })
    @DisplayName("Un ticket simple sans options doit retourner le prix de base")
    void computeTotal_singleTicketNoOptions_shouldReturnBasePrice(TicketType type, double expectedPrice) {
        List<TicketType> tickets = Collections.singletonList(type);
        PriceBreakdown breakdown = engine.computeTotal(tickets, DayOfWeek.MONDAY, false);
        
        assertEquals(expectedPrice, breakdown.getSubtotal(), 0.001);
        assertEquals(0.0, breakdown.getWednesdayDisc(), 0.001);
        assertEquals(0.0, breakdown.getThreeDSurcharge(), 0.001);
        assertEquals(0.0, breakdown.getGroupDisc(), 0.001);
        assertEquals(expectedPrice, breakdown.getTotal(), 0.001);
    }
    
    @Test
    @DisplayName("3D uniquement: les tickets prennent +2€")
    void computeTotal_3DOnly_singleTicket_shouldAdd2Euros() {
        List<TicketType> tickets = Collections.singletonList(TicketType.ADULT);
        PriceBreakdown breakdown = engine.computeTotal(tickets, DayOfWeek.MONDAY, true);
        
        assertEquals(10.00, breakdown.getSubtotal(), 0.001);
        assertEquals(0.0, breakdown.getWednesdayDisc(), 0.001);
        assertEquals(2.00, breakdown.getThreeDSurcharge(), 0.001);
        assertEquals(0.0, breakdown.getGroupDisc(), 0.001);
        assertEquals(12.00, breakdown.getTotal(), 0.001);
    }
    
    @Test
    @DisplayName("Réduction de groupe: plus de quatre ticket donne -10%")
    void computeTotal_groupOnly_fiveTickets_shouldApply10PercentDiscount() {
        List<TicketType> tickets = Arrays.asList(
            TicketType.ADULT, TicketType.ADULT, TicketType.CHILD, 
            TicketType.SENIOR, TicketType.STUDENT
        );
        PriceBreakdown breakdown = engine.computeTotal(tickets, DayOfWeek.THURSDAY, false);
        
        double expectedSubtotal = 10.00 + 10.00 + 6.00 + 7.50 + 8.00;
        double expectedGroupDisc = 41.50 * 0.10;
        double expectedTotal = 41.50 - 4.15;
        
        assertEquals(expectedSubtotal, breakdown.getSubtotal(), 0.001);
        assertEquals(0.0, breakdown.getWednesdayDisc(), 0.001);
        assertEquals(0.0, breakdown.getThreeDSurcharge(), 0.001);
        assertEquals(expectedGroupDisc, breakdown.getGroupDisc(), 0.001);
        assertEquals(expectedTotal, breakdown.getTotal(), 0.001);
    }
    
    @Test
    @DisplayName("Réduction de groupe: si il y a moins de quatre tickets, aucune réduction")
    void computeTotal_lessThan4Tickets_shouldNotApplyGroupDiscount() {
        List<TicketType> tickets = Arrays.asList(TicketType.ADULT, TicketType.CHILD, TicketType.SENIOR);
        PriceBreakdown breakdown = engine.computeTotal(tickets, DayOfWeek.MONDAY, false);
        
        assertEquals(0.0, breakdown.getGroupDisc(), 0.001);
    }
    
    @Test
    @DisplayName("Combination: Mercredi + 3D")
    void computeTotal_wednesdayAnd3D_shouldApplyInCorrectOrder() {
        List<TicketType> tickets = Arrays.asList(TicketType.ADULT, TicketType.CHILD);
        PriceBreakdown breakdown = engine.computeTotal(tickets, DayOfWeek.WEDNESDAY, true);
        
        assertEquals(16.00, breakdown.getSubtotal(), 0.001);
        assertEquals(3.20, breakdown.getWednesdayDisc(), 0.001);
        assertEquals(4.00, breakdown.getThreeDSurcharge(), 0.001);
        assertEquals(0.0, breakdown.getGroupDisc(), 0.001);
        assertEquals(16.80, breakdown.getTotal(), 0.001);
    }
    
    @Test
    @DisplayName("Combinaison: Mercredi + 3D + Groupe")
    void computeTotal_wednesdayAnd3DAndGroup_shouldApplyInCorrectOrder() {
        List<TicketType> tickets = Arrays.asList(
            TicketType.ADULT, TicketType.ADULT, TicketType.CHILD, TicketType.SENIOR
        );
        PriceBreakdown breakdown = engine.computeTotal(tickets, DayOfWeek.WEDNESDAY, true);
        
        assertEquals(33.50, breakdown.getSubtotal(), 0.001);
        assertEquals(6.70, breakdown.getWednesdayDisc(), 0.001);
        assertEquals(8.00, breakdown.getThreeDSurcharge(), 0.001);
        assertEquals(3.48, breakdown.getGroupDisc(), 0.001);
        assertEquals(31.32, breakdown.getTotal(), 0.001);
    }
    
    
    @Test
    @DisplayName("Doit retourner IllegalArgumentException quand la liste de tickets est nulle")
    void computeTotal_nullTickets_shouldThrow() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> engine.computeTotal(null, DayOfWeek.MONDAY, false)
        );
        assertTrue(exception.getMessage().contains("null"));
    }
    
    @Test
    @DisplayName("Doit retourner IllegalArgumentException quand le jour est nul")
    void computeTotal_nullDay_shouldThrow() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> engine.computeTotal(Collections.singletonList(TicketType.ADULT), null, false)
        );
        assertTrue(exception.getMessage().contains("null"));
    }
}
