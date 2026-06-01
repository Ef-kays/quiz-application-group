import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Main entrypoint, UI coordinator, and domain container for the Quiz Application.
 * This class houses the JavaFX UI layout systems, screen-switching methods,
 * nested modular classes (Question, Grade, QuizEngine), and even the complete
 * native automated test suite (AppTest) to ensure the entire application can be
 * compiled, tested, and run as a single file without any external dependencies.
 *
 * @author Group 2
 * @version 1.0
 */
public class App extends Application {

    private static final String APP_TITLE = "ProQuiz - Advanced Computer Science Assessment (Group 2)";
    private static final double DEFAULT_WIDTH = 960;
    private static final double DEFAULT_HEIGHT = 680;

    private QuizEngine quizEngine;
    private StackPane rootContainer;
    private Stage primaryStage;

    // View Components for real-time binding
    private Label studentLabel;
    private Label progressLabel;
    private Label scoreLabel;
    private ProgressBar progressBar;
    private Label questionIndexTitle;
    private Text questionText;
    private final List<Button> optionButtons = new ArrayList<>();
    private VBox dotNavigationContainer;
    private Button prevButton;
    private Button nextButton;

    /**
     * JavaFX application lifecycle start method. Sets up the primary container,
     * dimensions, global styles, and loads the initial welcome interface.
     *
     * @param stage the primary window stage
     * @throws Exception if a runtime initialization error occurs
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        this.quizEngine = new QuizEngine();
        this.rootContainer = new StackPane();

        Scene scene = new Scene(rootContainer, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        
        // Load the flat styles.css stylesheet located in the same src folder
        try {
            String cssPath = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("Warning: styles.css stylesheet not found in classpath. Falling back to default layout.");
        }

        showWelcomeScreen();

        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    /**
     * Renders and displays the Welcome interface to register the user's name.
     */
    public void showWelcomeScreen() {
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setSpacing(30);

        // Glassmorphic Card Container
        VBox card = new VBox();
        card.getStyleClass().add("glass-card");
        card.setSpacing(20);
        card.setPadding(new Insets(35));
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(480);

        // Header Title
        Text logoText = new Text("ProQuiz");
        logoText.getStyleClass().add("welcome-logo");

        Text groupLabel = new Text("Multiple Choice Assessment Suite — Group 2");
        groupLabel.getStyleClass().add("subtitle-text");

        // Rules List
        VBox infoPanel = new VBox();
        infoPanel.setSpacing(8);
        infoPanel.setAlignment(Pos.CENTER_LEFT);
        infoPanel.setPadding(new Insets(10, 0, 10, 0));

        Label rule1 = new Label("• Quiz consists of exactly 10 multiple choice questions.");
        rule1.getStyleClass().add("body-text");
        Label rule2 = new Label("• You can navigate between questions freely using navigation controls.");
        rule2.getStyleClass().add("body-text");
        Label rule3 = new Label("• Scores are compiled in real-time as answers are selected.");
        rule3.getStyleClass().add("body-text");
        Label rule4 = new Label("• Letter grades (A - F) will be computed on completion.");
        rule4.getStyleClass().add("body-text");

        infoPanel.getChildren().addAll(rule1, rule2, rule3, rule4);

        // Input Form
        VBox inputSection = new VBox();
        inputSection.setSpacing(8);
        inputSection.setAlignment(Pos.CENTER_LEFT);

        Label inputLabel = new Label("Enter Your Full Name to Begin:");
        inputLabel.getStyleClass().addAll("body-text", "label-white");
        inputLabel.setStyle("-fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("e.g. John Doe");
        nameField.getStyleClass().add("text-input");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #f43f5e; -fx-font-size: 12px; -fx-font-weight: bold;");
        errorLabel.setVisible(false);

        inputSection.getChildren().addAll(inputLabel, nameField, errorLabel);

        // Action buttons row
        HBox welcomeActionRow = new HBox(12);
        welcomeActionRow.setAlignment(Pos.CENTER);

        Button startButton = new Button("START ASSESSMENT");
        startButton.getStyleClass().add("btn-primary");
        startButton.setPrefWidth(220);

        Button testDiagnosticButton = new Button("RUN TESTS");
        testDiagnosticButton.getStyleClass().add("btn-secondary");
        testDiagnosticButton.setStyle("-fx-text-fill: #10b981; -fx-border-color: rgba(16, 185, 129, 0.4);");
        testDiagnosticButton.setPrefWidth(120);

        welcomeActionRow.getChildren().addAll(startButton, testDiagnosticButton);

        startButton.setOnAction(e -> {
            String inputName = nameField.getText();
            if (inputName == null || inputName.trim().isEmpty()) {
                nameField.setStyle("-fx-border-color: #f43f5e; -fx-background-color: rgba(244, 63, 94, 0.05);");
                errorLabel.setText("Please enter your name to proceed.");
                errorLabel.setVisible(true);
            } else {
                try {
                    nameField.setStyle("");
                    errorLabel.setVisible(false);
                    quizEngine.setUserName(inputName);
                    showQuizScreen();
                } catch (IllegalArgumentException | NullPointerException ex) {
                    showErrorDialog("Validation Error", ex.getMessage());
                }
            }
        });

        // Setup visual interactive diagnostics run
        testDiagnosticButton.setOnAction(e -> {
            try {
                AppTest.runAllTests();
                Alert successDialog = new Alert(Alert.AlertType.INFORMATION);
                successDialog.setTitle("Diagnostics Passed");
                successDialog.setHeaderText("Test Execution Successful");
                successDialog.setContentText("✓ All 6 automated modular diagnostic tests passed successfully! Check the terminal console output for details.");
                successDialog.showAndWait();
            } catch (Exception ex) {
                showErrorDialog("Tests Failed", ex.getMessage());
            }
        });

        card.getChildren().addAll(logoText, groupLabel, infoPanel, inputSection, welcomeActionRow);
        layout.getChildren().add(card);

        setContainerView(layout);
    }

    /**
     * Renders and displays the active quiz assessment page.
     */
    public void showQuizScreen() {
        BorderPane quizLayout = new BorderPane();
        quizLayout.setPadding(new Insets(20));

        // 1. HEADER PANEL
        VBox headerContainer = new VBox();
        headerContainer.setSpacing(10);
        headerContainer.setPadding(new Insets(0, 0, 20, 0));

        HBox topMetaRow = new HBox();
        topMetaRow.setAlignment(Pos.CENTER_LEFT);

        studentLabel = new Label("Student: " + quizEngine.getUserName());
        studentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #818cf8;");
        HBox.setHgrow(studentLabel, Priority.ALWAYS);
        studentLabel.setMaxWidth(Double.MAX_VALUE);

        progressLabel = new Label("Question 1 of 10");
        progressLabel.getStyleClass().add("label-slate");
        progressLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        scoreLabel = new Label("Live Score: 0/10");
        scoreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #10b981; -fx-padding: 0 0 0 20;");

        topMetaRow.getChildren().addAll(studentLabel, progressLabel, scoreLabel);

        progressBar = new ProgressBar(0.0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(10);

        headerContainer.getChildren().addAll(topMetaRow, progressBar);
        quizLayout.setTop(headerContainer);

        // 2. SIDEBAR NAVIGATION
        VBox sidebarContainer = new VBox();
        sidebarContainer.setAlignment(Pos.TOP_CENTER);
        sidebarContainer.setPadding(new Insets(10, 20, 10, 0));
        sidebarContainer.setSpacing(15);
        sidebarContainer.setPrefWidth(120);

        Label navLabel = new Label("NAVIGATOR");
        navLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #64748b; -fx-letter-spacing: 1px;");

        dotNavigationContainer = new VBox();
        dotNavigationContainer.setSpacing(10);
        dotNavigationContainer.setAlignment(Pos.CENTER);

        buildDotIndicators();

        sidebarContainer.getChildren().addAll(navLabel, dotNavigationContainer);
        quizLayout.setLeft(sidebarContainer);

        // 3. CENTRAL QUESTION CARD
        VBox centerContainer = new VBox();
        centerContainer.setSpacing(20);
        centerContainer.getStyleClass().add("glass-card");
        centerContainer.setPadding(new Insets(30));
        centerContainer.setAlignment(Pos.TOP_LEFT);

        questionIndexTitle = new Label("QUESTION 1");
        questionIndexTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #6366f1; -fx-letter-spacing: 1px;");

        questionText = new Text("Question text...");
        questionText.getStyleClass().add("heading-text");
        questionText.setWrappingWidth(680);

        VBox optionsContainer = new VBox();
        optionsContainer.setSpacing(12);
        optionsContainer.setPadding(new Insets(10, 0, 10, 0));

        optionButtons.clear();
        for (int i = 0; i < 4; i++) {
            final int index = i;
            Button optBtn = new Button();
            optBtn.getStyleClass().add("option-button");
            optBtn.setMaxWidth(Double.MAX_VALUE);
            optBtn.setOnAction(e -> handleOptionSelection(index));
            optionButtons.add(optBtn);
            optionsContainer.getChildren().add(optBtn);
        }

        centerContainer.getChildren().addAll(questionIndexTitle, questionText, optionsContainer);
        quizLayout.setCenter(centerContainer);

        // 4. FOOTER BUTTONS
        HBox footerContainer = new HBox();
        footerContainer.setPadding(new Insets(20, 0, 0, 0));
        footerContainer.setAlignment(Pos.CENTER_LEFT);

        prevButton = new Button("← PREVIOUS");
        prevButton.getStyleClass().add("btn-secondary");
        prevButton.setOnAction(e -> {
            if (quizEngine.previousQuestion()) {
                renderActiveQuestion();
            }
        });

        HBox.setHgrow(prevButton, Priority.NEVER);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        nextButton = new Button("NEXT →");
        nextButton.getStyleClass().add("btn-secondary");
        nextButton.setOnAction(e -> {
            if (quizEngine.nextQuestion()) {
                renderActiveQuestion();
            }
        });

        Button submitButton = new Button("SUBMIT QUIZ");
        submitButton.getStyleClass().add("btn-primary");
        submitButton.setOnAction(e -> handleSubmitAction());

        HBox footerButtonBox = new HBox(12);
        footerButtonBox.getChildren().addAll(nextButton, submitButton);

        footerContainer.getChildren().addAll(prevButton, spacer, footerButtonBox);
        quizLayout.setBottom(footerContainer);

        setContainerView(quizLayout);
        renderActiveQuestion();
    }

    /**
     * Draws the dots in the sidebar to jump directly to any question.
     */
    private void buildDotIndicators() {
        dotNavigationContainer.getChildren().clear();
        for (int i = 0; i < 10; i++) {
            final int questionIndex = i;
            HBox itemRow = new HBox(10);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            itemRow.setPadding(new Insets(2, 5, 2, 5));
            itemRow.setStyle("-fx-cursor: hand;");

            Label dot = new Label();
            dot.getStyleClass().add("nav-dot");

            Label numLabel = new Label("Question " + (questionIndex + 1));
            numLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #cbd5e1;");

            itemRow.getChildren().addAll(dot, numLabel);

            itemRow.setOnMouseClicked(e -> {
                quizEngine.setCurrentQuestionIndex(questionIndex);
                renderActiveQuestion();
            });

            dotNavigationContainer.getChildren().add(itemRow);
        }
    }

    /**
     * Binds and renders the values of the currently active question to the GUI.
     */
    private void renderActiveQuestion() {
        int currentIndex = quizEngine.getCurrentQuestionIndex();
        Question currentQuestion = quizEngine.getCurrentQuestion();

        questionIndexTitle.setText("QUESTION " + (currentIndex + 1) + " OF 10");
        questionText.setText(currentQuestion.getQuestionText());

        List<String> options = currentQuestion.getOptions();
        for (int i = 0; i < 4; i++) {
            Button btn = optionButtons.get(i);
            btn.setText((char)('A' + i) + ".   " + options.get(i));
            
            btn.getStyleClass().removeAll("option-button-selected", "option-button-correct", "option-button-incorrect");

            if (currentQuestion.getSelectedAnswerIndex() == i) {
                btn.getStyleClass().add("option-button-selected");
            }
        }

        progressLabel.setText("Question " + (currentIndex + 1) + " of 10");
        scoreLabel.setText("Live Score: " + quizEngine.calculateCurrentScore() + " / 10");

        double progressRatio = (double) quizEngine.getAnsweredCount() / 10;
        progressBar.setProgress(progressRatio);

        updateNavigationSidebar();

        prevButton.setDisable(currentIndex == 0);
        nextButton.setDisable(currentIndex == 9);

        assert currentIndex >= 0 && currentIndex < 10 : "Assertion Error: currentQuestionIndex out of range";
    }

    /**
     * Updates colors in the sidebar indicator matrix based on active/answered states.
     */
    private void updateNavigationSidebar() {
        int currentIndex = quizEngine.getCurrentQuestionIndex();
        List<Question> questions = quizEngine.getQuestions();

        for (int i = 0; i < 10; i++) {
            HBox itemRow = (HBox) dotNavigationContainer.getChildren().get(i);
            Label dot = (Label) itemRow.getChildren().get(0);
            Label label = (Label) itemRow.getChildren().get(1);

            dot.getStyleClass().removeAll("nav-dot-active", "nav-dot-answered");
            label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #cbd5e1;");

            if (i == currentIndex) {
                dot.getStyleClass().add("nav-dot-active");
                label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #818cf8;");
            } else if (questions.get(i).isAnswered()) {
                dot.getStyleClass().add("nav-dot-answered");
                label.setStyle("-fx-font-size: 13px; -fx-font-weight: normal; -fx-text-fill: #06b6d4;");
            }
        }
    }

    /**
     * Records option index and executes instant dynamic scoring updates.
     *
     * @param optionIndex the clicked option index (0 to 3)
     */
    private void handleOptionSelection(int optionIndex) {
        int currentIndex = quizEngine.getCurrentQuestionIndex();
        quizEngine.selectAnswer(currentIndex, optionIndex);
        renderActiveQuestion();
    }

    /**
     * Validates remaining questions and performs scene submit warning/transitions.
     */
    private void handleSubmitAction() {
        int unansweredCount = 10 - quizEngine.getAnsweredCount();

        if (unansweredCount > 0) {
            Alert warning = new Alert(Alert.AlertType.CONFIRMATION);
            warning.setTitle("Incomplete Assessment");
            warning.setHeaderText("You have " + unansweredCount + " unanswered questions!");
            warning.setContentText("Are you sure you want to submit the quiz and receive a grade based on your current answers?");
            
            Optional<ButtonType> result = warning.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                showResultScreen();
            }
        } else {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Complete Assessment");
            confirmation.setHeaderText("Submit Quiz");
            confirmation.setContentText("Are you ready to submit your answers and view your final grade sheet?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                showResultScreen();
            }
        }
    }

    /**
     * Renders and displays the final Results dashboard scene.
     */
    public void showResultScreen() {
        VBox layout = new VBox();
        layout.setPadding(new Insets(30));
        layout.setSpacing(25);
        layout.setAlignment(Pos.TOP_CENTER);

        // Header Title
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        
        Text title = new Text("Assessment Summary");
        title.getStyleClass().add("title-text");

        Text subtitle = new Text("Congratulations, " + quizEngine.getUserName() + "! You have finished the quiz.");
        subtitle.getStyleClass().add("subtitle-text");
        subtitle.setStyle("-fx-font-size: 15px; -fx-fill: #cbd5e1;");

        headerBox.getChildren().addAll(title, subtitle);

        // SCORE & GRADE TILES
        HBox dashboardGrid = new HBox(20);
        dashboardGrid.setAlignment(Pos.CENTER);
        dashboardGrid.setMaxWidth(800);

        int finalScore = quizEngine.calculateCurrentScore();
        Grade finalGrade = quizEngine.calculateGrade();

        // Radial progress
        VBox scoreCard = new VBox(15);
        scoreCard.getStyleClass().add("glass-card");
        scoreCard.setAlignment(Pos.CENTER);
        scoreCard.setPadding(new Insets(20));
        HBox.setHgrow(scoreCard, Priority.ALWAYS);
        scoreCard.setMinWidth(360);

        Label scoreTitle = new Label("TOTAL SCORE");
        scoreTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #64748b; -fx-letter-spacing: 1px;");

        StackPane circularProgress = new StackPane();
        circularProgress.getStyleClass().add("score-circle-container");

        Circle bgCircle = new Circle(60);
        bgCircle.getStyleClass().add("score-circle-bg");

        Arc fillArc = new Arc();
        fillArc.setCenterX(0);
        fillArc.setCenterY(0);
        fillArc.setRadiusX(60);
        fillArc.setRadiusY(60);
        fillArc.setStartAngle(90);
        double angleLength = -((double) finalScore / 10.0) * 360.0;
        fillArc.setLength(angleLength);
        fillArc.setType(ArcType.OPEN);
        fillArc.getStyleClass().add("score-circle-fill");

        VBox textOverlay = new VBox(0);
        textOverlay.setAlignment(Pos.CENTER);
        
        Text numText = new Text(String.valueOf(finalScore));
        numText.getStyleClass().add("score-text");
        
        Text totalText = new Text("of 10 correct");
        totalText.getStyleClass().add("total-score-text");
        
        textOverlay.getChildren().addAll(numText, totalText);

        circularProgress.getChildren().addAll(bgCircle, fillArc, textOverlay);

        Label scorePercentageLabel = new Label((finalScore * 10) + "% Completion Accuracy");
        scorePercentageLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #06b6d4;");

        scoreCard.getChildren().addAll(scoreTitle, circularProgress, scorePercentageLabel);

        // Grade badge
        VBox gradeCard = new VBox(10);
        gradeCard.getStyleClass().add("glass-card");
        gradeCard.setAlignment(Pos.CENTER);
        gradeCard.setPadding(new Insets(20));
        HBox.setHgrow(gradeCard, Priority.ALWAYS);
        gradeCard.setMinWidth(360);

        Label gradeTitle = new Label("FINAL GRADE");
        gradeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #64748b; -fx-letter-spacing: 1px;");

        Text gradeBadge = new Text(finalGrade.name());
        gradeBadge.getStyleClass().add("grade-badge");
        gradeBadge.getStyleClass().add("grade-badge-" + finalGrade.name().toLowerCase());

        Label performanceLabel = new Label(finalGrade.getLabel());
        performanceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #f8fafc;");

        Label descLabel = new Label(finalGrade.getDescription());
        descLabel.getStyleClass().add("body-text");
        descLabel.setStyle("-fx-text-alignment: center; -fx-font-size: 13px;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);

        gradeCard.getChildren().addAll(gradeTitle, gradeBadge, performanceLabel, descLabel);
        dashboardGrid.getChildren().addAll(scoreCard, gradeCard);

        // REVIEW ACCORDION
        VBox reviewSection = new VBox(10);
        reviewSection.setAlignment(Pos.TOP_LEFT);
        reviewSection.setMaxWidth(800);
        VBox.setVgrow(reviewSection, Priority.ALWAYS);

        Label reviewTitle = new Label("DETAILED RESPONSE REVIEW");
        reviewTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #64748b; -fx-letter-spacing: 1px; -fx-padding: 10 0 5 0;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setPrefHeight(200);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox reviewListContainer = new VBox(12);
        reviewListContainer.setPadding(new Insets(2, 5, 2, 2));

        List<Question> questionList = quizEngine.getQuestions();
        for (int i = 0; i < questionList.size(); i++) {
            Question q = questionList.get(i);
            
            VBox itemCard = new VBox(10);
            itemCard.getStyleClass().add("review-item");

            HBox itemHeader = new HBox(10);
            itemHeader.setAlignment(Pos.CENTER_LEFT);

            Label indexBadge = new Label("Question " + (i + 1));
            indexBadge.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #818cf8;");
            HBox.setHgrow(indexBadge, Priority.ALWAYS);
            indexBadge.setMaxWidth(Double.MAX_VALUE);

            Label statusIndicator = new Label();
            statusIndicator.getStyleClass().add("status-indicator");

            if (q.isCorrect()) {
                itemCard.getStyleClass().add("review-item-correct");
                statusIndicator.setText("✓ CORRECT");
                statusIndicator.getStyleClass().add("status-indicator-correct");
            } else {
                itemCard.getStyleClass().add("review-item-incorrect");
                statusIndicator.setText(q.isAnswered() ? "✗ INCORRECT" : "⚠ UNANSWERED");
                statusIndicator.getStyleClass().add("status-indicator-incorrect");
            }

            itemHeader.getChildren().addAll(indexBadge, statusIndicator);

            Text qText = new Text(q.getQuestionText());
            qText.getStyleClass().add("body-text");
            qText.setStyle("-fx-font-weight: bold; -fx-fill: #f8fafc; -fx-font-size: 14px;");
            qText.setWrappingWidth(730);

            VBox answersBox = new VBox(4);
            answersBox.setPadding(new Insets(2, 0, 2, 10));

            String selectedStr = q.isAnswered() ? q.getOptions().get(q.getSelectedAnswerIndex()) : "None";
            String correctStr = q.getOptions().get(q.getCorrectAnswerIndex());

            Label userAnsLabel = new Label("Your selection:  " + selectedStr);
            userAnsLabel.getStyleClass().add("body-text");
            userAnsLabel.setStyle(q.isCorrect() ? "-fx-text-fill: #a7f3d0;" : "-fx-text-fill: #fecdd3;");

            Label correctAnsLabel = new Label("Correct answer:  " + correctStr);
            correctAnsLabel.getStyleClass().add("body-text");
            correctAnsLabel.setStyle("-fx-text-fill: #34d399; -fx-font-weight: bold;");

            answersBox.getChildren().addAll(userAnsLabel);
            if (!q.isCorrect()) {
                answersBox.getChildren().add(correctAnsLabel);
            }

            itemCard.getChildren().addAll(itemHeader, qText, answersBox);
            reviewListContainer.getChildren().add(itemCard);
        }

        scrollPane.setContent(reviewListContainer);
        reviewSection.getChildren().addAll(reviewTitle, scrollPane);

        // FOOTER ACTIONS
        HBox actionRow = new HBox(15);
        actionRow.setAlignment(Pos.CENTER);

        Button retakeBtn = new Button("RETAKE QUIZ");
        retakeBtn.getStyleClass().add("btn-primary");
        retakeBtn.setPrefWidth(220);
        retakeBtn.setOnAction(e -> {
            quizEngine.reset();
            showWelcomeScreen();
        });

        Button closeBtn = new Button("CLOSE APPLICATION");
        closeBtn.getStyleClass().add("btn-secondary");
        closeBtn.setPrefWidth(220);
        closeBtn.setOnAction(e -> System.exit(0));

        actionRow.getChildren().addAll(retakeBtn, closeBtn);

        layout.getChildren().addAll(headerBox, dashboardGrid, reviewSection, actionRow);

        setContainerView(layout);

        assert finalScore >= 0 && finalScore <= 10 : "Assertion Error: finalScore out of bounds in ResultView";
        assert finalGrade == Grade.fromScore(finalScore) : "Assertion Error: Grade mapping mismatch in ResultView";
    }

    /**
     * Swaps the active UI screen inside the root StackPane.
     *
     * @param viewNode the root layout component of the new screen
     */
    private void setContainerView(Parent viewNode) {
        assert viewNode != null : "Assertion Error: Attempted to set a null container view";
        rootContainer.getChildren().clear();
        rootContainer.getChildren().add(viewNode);
    }

    /**
     * Renders standard validation error alerts.
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Action Invalid");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Helper entry launcher method. Supports a CLI test execution argument flag.
     *
     * @param args runtime execution arguments
     */
    public static void main(String[] args) {
        if (args.length > 0 && (args[0].equalsIgnoreCase("-test") || args[0].equalsIgnoreCase("--test"))) {
            AppTest.runAllTests();
            System.exit(0);
        }
        launch(args);
    }

    // =========================================================================
    //                    MODULAR LOGIC & DOMAIN MODELS
    // =========================================================================

    /**
     * Inner model class representing a multiple-choice question.
     * Satisfies the modular architecture and encapsulation constraints.
     */
    public static class Question {
        private final String questionText;
        private final List<String> options;
        private final int correctAnswerIndex;
        private int selectedAnswerIndex;

        /**
         * Constructs a new question item.
         *
         * @param questionText       the text of the question (must be non-null and non-empty)
         * @param options            a list of exactly 4 choices (must be non-null, non-empty)
         * @param correctAnswerIndex correct 0-based option index (0 to 3)
         * @throws NullPointerException     if parameters are null
         * @throws IllegalArgumentException if dimensions are incorrect
         */
        public Question(String questionText, List<String> options, int correctAnswerIndex) {
            if (questionText == null) {
                throw new NullPointerException("Question text cannot be null.");
            }
            if (questionText.trim().isEmpty()) {
                throw new IllegalArgumentException("Question text cannot be empty.");
            }
            if (options == null) {
                throw new NullPointerException("Options list cannot be null.");
            }
            if (options.size() != 4) {
                throw new IllegalArgumentException("Must have exactly 4 choices.");
            }
            for (int i = 0; i < options.size(); i++) {
                String option = options.get(i);
                if (option == null || option.trim().isEmpty()) {
                    throw new IllegalArgumentException("Option choice " + i + " must not be empty.");
                }
            }
            if (correctAnswerIndex < 0 || correctAnswerIndex > 3) {
                throw new IllegalArgumentException("Correct answer index must be between 0 and 3.");
            }

            assert correctAnswerIndex >= 0 && correctAnswerIndex <= 3 : "Assertion Error: correct index bounds";

            this.questionText = questionText;
            this.options = Collections.unmodifiableList(new ArrayList<>(options));
            this.correctAnswerIndex = correctAnswerIndex;
            this.selectedAnswerIndex = -1;
        }

        public String getQuestionText() {
            return questionText;
        }

        public List<String> getOptions() {
            return options;
        }

        public int getCorrectAnswerIndex() {
            assert correctAnswerIndex >= 0 && correctAnswerIndex <= 3 : "Invariant violated: correctAnswerIndex";
            return correctAnswerIndex;
        }

        public int getSelectedAnswerIndex() {
            assert selectedAnswerIndex >= -1 && selectedAnswerIndex <= 3 : "Invariant violated: selectedAnswerIndex";
            return selectedAnswerIndex;
        }

        public void setSelectedAnswerIndex(int selectedAnswerIndex) {
            if (selectedAnswerIndex < 0 || selectedAnswerIndex > 3) {
                throw new IllegalArgumentException("Selected index must be between 0 and 3.");
            }
            this.selectedAnswerIndex = selectedAnswerIndex;
            assert this.selectedAnswerIndex == selectedAnswerIndex : "Assertion Error: selected answer save failed";
        }

        public boolean isAnswered() {
            return selectedAnswerIndex != -1;
        }

        public boolean isCorrect() {
            if (!isAnswered()) {
                return false;
            }
            boolean isCorrect = selectedAnswerIndex == correctAnswerIndex;
            assert !isCorrect || (selectedAnswerIndex == correctAnswerIndex) : "Assertion Error: evaluation error";
            return isCorrect;
        }

        public void reset() {
            this.selectedAnswerIndex = -1;
            assert this.selectedAnswerIndex == -1 : "Assertion Error: reset question failed";
        }
    }

    /**
     * Inner enum class mapping correct scores to letter grades.
     * Evaluates boundary grades as specified in the scoring table.
     */
    public static enum Grade {
        A("Outstanding", "Excellent work! You have shown a strong command of the concepts."),
        B("Very Good", "Great job! A very solid performance."),
        C("Good", "Good effort! With a little more review, you can master this."),
        D("Satisfactory", "Passed. You got some core concepts, but there is room for improvement."),
        E("Pass", "Barely passed. Consider reviewing the material thoroughly."),
        F("Fail", "Needs Improvement. We recommend revisiting the fundamental concepts and retaking the quiz.");

        private final String label;
        private final String description;

        Grade(String label, String description) {
            this.label = label;
            this.description = description;
        }

        public String getLabel() {
            return label;
        }

        public String getDescription() {
            return description;
        }

        /**
         * Resolves the appropriate Grade enum matching the total score.
         *
         * @param score total correct answers count (must be in range 0 to 10)
         * @return the resolved Grade constant
         * @throws IllegalArgumentException if score is out of bounds
         */
        public static Grade fromScore(int score) {
            if (score < 0 || score > 10) {
                throw new IllegalArgumentException("Score must be between 0 and 10.");
            }

            assert score >= 0 && score <= 10 : "Assertion Error: score validation out of bounds";

            Grade grade;
            if (score >= 7) {
                grade = A;
            } else if (score == 6) {
                grade = B;
            } else if (score == 5) {
                grade = C;
            } else if (score == 4) {
                grade = D;
            } else if (score == 3) {
                grade = E;
            } else {
                grade = F;
            }

            // Verify mapping consistency
            assert (score >= 7 && grade == A) ||
                   (score == 6 && grade == B) ||
                   (score == 5 && grade == C) ||
                   (score == 4 && grade == D) ||
                   (score == 3 && grade == E) ||
                   (score <= 2 && grade == F) : "Assertion Error: grading rules invariant breached";

            return grade;
        }
    }

    /**
     * Inner model managing the quiz states, navigation index, and question data banks.
     */
    public static class QuizEngine {
        private static final int TOTAL_QUESTIONS_COUNT = 10;
        private final List<Question> questions;
        private int currentQuestionIndex;
        private String userName;

        /**
         * Initializes and preloads the 10-question data bank.
         */
        public QuizEngine() {
            this.questions = new ArrayList<>();
            this.currentQuestionIndex = 0;
            this.userName = "Anonymous";
            preloadQuestions();

            assert this.questions.size() == TOTAL_QUESTIONS_COUNT : "Assertion Error: question preload size mismatch";
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            if (userName == null) {
                throw new NullPointerException("User name cannot be null.");
            }
            if (userName.trim().isEmpty()) {
                throw new IllegalArgumentException("User name cannot be empty.");
            }
            this.userName = userName.trim();
            assert this.userName.equals(userName.trim()) : "Assertion Error: user name set failed";
        }

        private void preloadQuestions() {
            questions.add(new Question(
                    "Which of the following is NOT a valid access modifier in Java?",
                    Arrays.asList("public", "private", "protected", "internal"),
                    3
            ));

            questions.add(new Question(
                    "What is the size of a float variable in Java?",
                    Arrays.asList("8 bits", "16 bits", "32 bits", "64 bits"),
                    2
            ));

            questions.add(new Question(
                    "Which Java keyword is used to prevent a method from being overridden?",
                    Arrays.asList("static", "final", "abstract", "synchronized"),
                    1
            ));

            questions.add(new Question(
                    "In Java, what is the primary role of the Garbage Collector?",
                    Arrays.asList("Destroy compile-time errors", "Reclaim unused heap memory", "Defragment the hard disk", "Validate input statements"),
                    1
            ));

            questions.add(new Question(
                    "Which data structure operates on a Last-In, First-Out (LIFO) principle?",
                    Arrays.asList("Queue", "Stack", "LinkedList", "HashMap"),
                    1
            ));

            questions.add(new Question(
                    "Which Java operator is used to check if an object is an instance of a specific class?",
                    Arrays.asList("instanceof", "isinstance", "typeof", "extends"),
                    0
            ));

            questions.add(new Question(
                    "What is the default value of a local variable in Java?",
                    Arrays.asList("0", "null", "false", "It does not have a default value and must be initialized before use"),
                    3
            ));

            questions.add(new Question(
                    "Which interface must a class implement to allow its objects to be sorted using Collections.sort()?",
                    Arrays.asList("Runnable", "Serializable", "Comparable", "Cloneable"),
                    2
            ));

            questions.add(new Question(
                    "What exception is thrown when an application attempts to use null where an object is required?",
                    Arrays.asList("NullPointerException", "IllegalArgumentException", "IndexOutOfBoundsException", "ArithmeticException"),
                    0
            ));

            questions.add(new Question(
                    "Which command is used to execute a compiled Java class file from the command line?",
                    Arrays.asList("javac", "java", "javadoc", "jar"),
                    1
            ));
        }

        public List<Question> getQuestions() {
            return Collections.unmodifiableList(questions);
        }

        public int getCurrentQuestionIndex() {
            assert currentQuestionIndex >= 0 && currentQuestionIndex < TOTAL_QUESTIONS_COUNT : "Invariant violated: currentQuestionIndex";
            return currentQuestionIndex;
        }

        public Question getCurrentQuestion() {
            return questions.get(getCurrentQuestionIndex());
        }

        public void selectAnswer(int questionIndex, int answerIndex) {
            if (questionIndex < 0 || questionIndex >= TOTAL_QUESTIONS_COUNT) {
                throw new IndexOutOfBoundsException("Question index out of range.");
            }
            if (answerIndex < 0 || answerIndex > 3) {
                throw new IllegalArgumentException("Answer index must be between 0 and 3.");
            }
            questions.get(questionIndex).setSelectedAnswerIndex(answerIndex);
            assert questions.get(questionIndex).getSelectedAnswerIndex() == answerIndex : "Assertion Error: failed to record selection";
        }

        public boolean nextQuestion() {
            if (currentQuestionIndex < TOTAL_QUESTIONS_COUNT - 1) {
                currentQuestionIndex++;
                assert currentQuestionIndex < TOTAL_QUESTIONS_COUNT : "Assertion Error: current index out of bounds after next";
                return true;
            }
            return false;
        }

        public boolean previousQuestion() {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                assert currentQuestionIndex >= 0 : "Assertion Error: current index out of bounds after prev";
                return true;
            }
            return false;
        }

        public void setCurrentQuestionIndex(int index) {
            if (index < 0 || index >= TOTAL_QUESTIONS_COUNT) {
                throw new IllegalArgumentException("Question index out of bounds.");
            }
            this.currentQuestionIndex = index;
            assert this.currentQuestionIndex == index : "Assertion Error: jump navigation failed";
        }

        public int calculateCurrentScore() {
            int score = 0;
            for (Question q : questions) {
                if (q.isCorrect()) {
                    score++;
                }
            }
            assert score >= 0 && score <= TOTAL_QUESTIONS_COUNT : "Assertion Error: calculated score bounds error";
            return score;
        }

        public int getAnsweredCount() {
            int count = 0;
            for (Question q : questions) {
                if (q.isAnswered()) {
                    count++;
                }
            }
            return count;
        }

        public Grade calculateGrade() {
            return Grade.fromScore(calculateCurrentScore());
        }

        public void reset() {
            for (Question q : questions) {
                q.reset();
            }
            currentQuestionIndex = 0;
            userName = "Anonymous";

            assert currentQuestionIndex == 0 : "Assertion Error: reset failed to restore question index to 0";
            assert calculateCurrentScore() == 0 : "Assertion Error: reset failed to clear scores";
        }
    }

    // =========================================================================
    //                    AUTOMATED NATIVE UNIT TESTS (ZERO DEPENDENCY)
    // =========================================================================

    /**
     * Automated native diagnostic unit test suite. Runs directly using standard Java
     * Assertions without requiring any external libraries (like JUnit) to be on the classpath.
     */
    public static class AppTest {
        
        /**
         * Executes all 6 modular diagnostic test cases.
         *
         * @throws RuntimeException if any validation assertion fails
         */
        public static void runAllTests() {
            System.out.println("\n========================================================");
            System.out.println("  STARTING AUTOMATED NATIVE ASSESSMENT DIAGNOSTICS      ");
            System.out.println("========================================================");
            try {
                AppTest suite = new AppTest();
                
                System.out.print("Running testInitialState()................. ");
                suite.setUp();
                suite.testInitialState();
                System.out.println("[ PASSED ]");
                
                System.out.print("Running testUserNameValidation()............ ");
                suite.setUp();
                suite.testUserNameValidation();
                System.out.println("[ PASSED ]");
                
                System.out.print("Running testNavigationBounds()............. ");
                suite.setUp();
                suite.testNavigationBounds();
                System.out.println("[ PASSED ]");
                
                System.out.print("Running testAnswerRegistration()........... ");
                suite.setUp();
                suite.testAnswerRegistration();
                System.out.println("[ PASSED ]");
                
                System.out.print("Running testGradingMapping()............... ");
                suite.setUp();
                suite.testGradingMapping();
                System.out.println("[ PASSED ]");
                
                System.out.print("Running testResetState()................... ");
                suite.setUp();
                suite.testResetState();
                System.out.println("[ PASSED ]");
                
                System.out.println("========================================================");
                System.out.println("  SUCCESS: ALL 6 DIAGNOSTIC TEST CASES COMPLETED        ");
                System.out.println("========================================================");
            } catch (Throwable t) {
                System.err.println("\n!!! DIAGNOSTIC VERIFICATION FAILED !!!");
                System.err.println("Failure reason: " + t.getMessage());
                System.err.println("Stack trace details:");
                t.printStackTrace();
                throw new RuntimeException("Assertion verification aborted. Logic check failed!", t);
            }
        }

        private QuizEngine quizEngine;

        public void setUp() {
            this.quizEngine = new QuizEngine();
        }

        public void testInitialState() {
            assertEquals("Anonymous", quizEngine.getUserName());
            assertEquals(0, quizEngine.getCurrentQuestionIndex());
            assertEquals(10, quizEngine.getQuestions().size());
            assertEquals(0, quizEngine.calculateCurrentScore());
            assertEquals(0, quizEngine.getAnsweredCount());
        }

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

        public void testNavigationBounds() {
            assertEquals(0, quizEngine.getCurrentQuestionIndex());

            assertFalse(quizEngine.previousQuestion());
            assertEquals(0, quizEngine.getCurrentQuestionIndex());

            assertTrue(quizEngine.nextQuestion());
            assertEquals(1, quizEngine.getCurrentQuestionIndex());

            quizEngine.setCurrentQuestionIndex(9);
            assertEquals(9, quizEngine.getCurrentQuestionIndex());

            assertFalse(quizEngine.nextQuestion());
            assertEquals(9, quizEngine.getCurrentQuestionIndex());

            assertThrows(IllegalArgumentException.class, () -> {
                quizEngine.setCurrentQuestionIndex(-1);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                quizEngine.setCurrentQuestionIndex(10);
            });
        }

        public void testAnswerRegistration() {
            quizEngine.selectAnswer(0, 3);
            assertTrue(quizEngine.getQuestions().get(0).isAnswered());
            assertTrue(quizEngine.getQuestions().get(0).isCorrect());
            assertEquals(1, quizEngine.calculateCurrentScore());
            assertEquals(1, quizEngine.getAnsweredCount());

            quizEngine.selectAnswer(1, 1);
            assertTrue(quizEngine.getQuestions().get(1).isAnswered());
            assertFalse(quizEngine.getQuestions().get(1).isCorrect());
            assertEquals(1, quizEngine.calculateCurrentScore());
            assertEquals(2, quizEngine.getAnsweredCount());

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

        public void testGradingMapping() {
            assertEquals(Grade.A, Grade.fromScore(10));
            assertEquals(Grade.A, Grade.fromScore(7));

            assertEquals(Grade.B, Grade.fromScore(6));
            assertEquals(Grade.C, Grade.fromScore(5));
            assertEquals(Grade.D, Grade.fromScore(4));
            assertEquals(Grade.E, Grade.fromScore(3));

            assertEquals(Grade.F, Grade.fromScore(2));
            assertEquals(Grade.F, Grade.fromScore(1));
            assertEquals(Grade.F, Grade.fromScore(0));

            assertThrows(IllegalArgumentException.class, () -> {
                Grade.fromScore(-1);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                Grade.fromScore(11);
            });
        }

        public void testResetState() {
            quizEngine.setUserName("Alice");
            quizEngine.selectAnswer(0, 3);
            quizEngine.selectAnswer(1, 1);
            quizEngine.setCurrentQuestionIndex(5);

            quizEngine.reset();

            assertEquals("Anonymous", quizEngine.getUserName());
            assertEquals(0, quizEngine.getCurrentQuestionIndex());
            assertEquals(0, quizEngine.calculateCurrentScore());
            assertEquals(0, quizEngine.getAnsweredCount());
            for (Question q : quizEngine.getQuestions()) {
                assertFalse(q.isAnswered());
            }
        }

        // ==========================================
        //         CUSTOM ASSERTION HELPERS
        // ==========================================
        private static void assertEquals(Object expected, Object actual) {
            if (!java.util.Objects.equals(expected, actual)) {
                throw new AssertionError("Assertion Failed: Expected '" + expected + "' but got '" + actual + "'");
            }
        }

        private static void assertTrue(boolean condition) {
            if (!condition) {
                throw new AssertionError("Assertion Failed: Expected 'true' but got 'false'");
            }
        }

        private static void assertFalse(boolean condition) {
            if (condition) {
                throw new AssertionError("Assertion Failed: Expected 'false' but got 'true'");
            }
        }

        private static void assertThrows(Class<? extends Throwable> expectedException, Runnable runnable) {
            try {
                runnable.run();
                throw new AssertionError("Assertion Failed: Expected exception " + expectedException.getName() + " was not thrown.");
            } catch (Throwable t) {
                if (!expectedException.isInstance(t)) {
                    throw new AssertionError("Assertion Failed: Expected exception " + expectedException.getName() + " but got " + t.getClass().getName());
                }
            }
        }
    }
}
