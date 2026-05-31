import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated test suite verifying the nested modular logic inside App.java.
 *
 * @author Group 2
 * @version 1.0
 */
public class AppTest {

    private App.QuizEngine quizEngine;

    @BeforeEach
    public void setUp() {
        quizEngine = new App.QuizEngine();
    }

    /**
     * Verifies that the initial state of the quiz engine is correct.
     */
    @Test
    public void testInitialState() {
        assertEquals("Anonymous", quizEngine.getUserName());
        assertEquals(0, quizEngine.getCurrentQuestionIndex());
        assertEquals(10, quizEngine.getQuestions().size());
        assertEquals(0, quizEngine.calculateCurrentScore());
        assertEquals(0, quizEngine.getAnsweredCount());
    }

    /**
     * Verifies that the username can be set correctly, and validates exception triggers
     * for invalid inputs.
     */
    @Test
    public void testUserNameValidation() {
        quizEngine.setUserName("  Alice Smith  ");
        assertEquals("Alice Smith", quizEngine.getUserName());

        assertThrows(NullPointerException.class, () -> {
            quizEngine.setUserName(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            quizEngine.setUserName("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            quizEngine.setUserName("   ");
        });
    }

    /**
     * Verifies that the navigation functions properly and safely caps at boundaries.
     */
    @Test
    public void testNavigationBounds() {
        assertEquals(0, quizEngine.getCurrentQuestionIndex());

        // Navigating previous at beginning should fail
        assertFalse(quizEngine.previousQuestion());
        assertEquals(0, quizEngine.getCurrentQuestionIndex());

        // Navigate forward
        assertTrue(quizEngine.nextQuestion());
        assertEquals(1, quizEngine.getCurrentQuestionIndex());

        // Jump to last question
        quizEngine.setCurrentQuestionIndex(9);
        assertEquals(9, quizEngine.getCurrentQuestionIndex());

        // Navigating next at the end should fail
        assertFalse(quizEngine.nextQuestion());
        assertEquals(9, quizEngine.getCurrentQuestionIndex());

        // Test invalid direct navigation jumps
        assertThrows(IllegalArgumentException.class, () -> {
            quizEngine.setCurrentQuestionIndex(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            quizEngine.setCurrentQuestionIndex(10);
        });
    }

    /**
     * Verifies that selecting answers updates the scores and completion metrics properly.
     */
    @Test
    public void testAnswerRegistration() {
        // Answer first question correctly (correct answer is index 3)
        quizEngine.selectAnswer(0, 3);
        assertTrue(quizEngine.getQuestions().get(0).isAnswered());
        assertTrue(quizEngine.getQuestions().get(0).isCorrect());
        assertEquals(1, quizEngine.calculateCurrentScore());
        assertEquals(1, quizEngine.getAnsweredCount());

        // Answer second question incorrectly (correct answer is index 2, select 1)
        quizEngine.selectAnswer(1, 1);
        assertTrue(quizEngine.getQuestions().get(1).isAnswered());
        assertFalse(quizEngine.getQuestions().get(1).isCorrect());
        assertEquals(1, quizEngine.calculateCurrentScore());
        assertEquals(2, quizEngine.getAnsweredCount());

        // Test out-of-bounds parameters
        assertThrows(IndexOutOfBoundsException.class, () -> {
            quizEngine.selectAnswer(-1, 0);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            quizEngine.selectAnswer(10, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            quizEngine.selectAnswer(0, -1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            quizEngine.selectAnswer(0, 4);
        });
    }

    /**
     * Verifies that the grading mappings (A, B, C, D, E, F) accurately match all
     * boundary scores from 0 to 10 as per the user's grading table.
     */
    @Test
    public void testGradingMapping() {
        // Score 7-10 -> A
        assertEquals(App.Grade.A, App.Grade.fromScore(10));
        assertEquals(App.Grade.A, App.Grade.fromScore(7));

        // Score 6 -> B
        assertEquals(App.Grade.B, App.Grade.fromScore(6));

        // Score 5 -> C
        assertEquals(App.Grade.C, App.Grade.fromScore(5));

        // Score 4 -> D
        assertEquals(App.Grade.D, App.Grade.fromScore(4));

        // Score 3 -> E
        assertEquals(App.Grade.E, App.Grade.fromScore(3));

        // Score 0-2 -> F
        assertEquals(App.Grade.F, App.Grade.fromScore(2));
        assertEquals(App.Grade.F, App.Grade.fromScore(1));
        assertEquals(App.Grade.F, App.Grade.fromScore(0));

        // Validate out of bound grading values
        assertThrows(IllegalArgumentException.class, () -> {
            App.Grade.fromScore(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            App.Grade.fromScore(11);
        });
    }

    /**
     * Tests that resetting the quiz successfully wipes all recorded inputs.
     */
    @Test
    public void testResetState() {
        quizEngine.setUserName("Alice");
        quizEngine.selectAnswer(0, 3);
        quizEngine.selectAnswer(1, 1);
        quizEngine.setCurrentQuestionIndex(5);

        // Run reset
        quizEngine.reset();

        assertEquals("Anonymous", quizEngine.getUserName());
        assertEquals(0, quizEngine.getCurrentQuestionIndex());
        assertEquals(0, quizEngine.calculateCurrentScore());
        assertEquals(0, quizEngine.getAnsweredCount());
        for (App.Question q : quizEngine.getQuestions()) {
            assertFalse(q.isAnswered());
        }
    }
}
