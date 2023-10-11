@usingMockedOpenAiService
@startWithEmptyDownloadsFolder
Feature: Generate Training examples for fine-tuning OpenAI
  As an admin,
  I want the users to be able to suggest good note questions or improvements for bad ones,
  So that I can use these data for OpenAI fine-tuning to improve question generation.

  Background:
    Given I am logged in as an existing user
    And I have a note with the topic "Who Let the Dogs Out"
    And OpenAI by default returns this question:
      | Question Stem                     | Correct Choice | Incorrect Choice 1 |
      | Who wrote 'Who Let the Dogs Out'? | Anslem Douglas | Baha Men           |
    And I ask to generate a question for the note "Who Let the Dogs Out"


  Scenario: Admin should be able to generate training data from suggested questions
    When I suggest the displayed question "Who wrote 'Who Let the Dogs Out'?" as a good example
    Then an admin should be able to download the training data containing 1 example containing "Who wrote 'Who Let the Dogs Out'?"
    Then an admin should be able to download the training data containing 1 example containing "Baha Men"

  @ignore
  Scenario Outline: Admin should be able to generate training data from questions with good feedback
    When I suggest the displayed question "Who wrote 'Who Let the Dogs Out'?" as a <Feedback> example
    Then I should see a message saying the feedback was sent successfully
    And an admin should be able to download the training data containing <Number_of_example_download> examples

    Examples:

    |Feedback| Number_of_example_download|
    |good    | 1                         |
    |bad     | 0                         |

  @ignore
  Scenario: Admin should be able to generate training data from questions with good feedback and comment
    When I suggest the displayed question "Who wrote 'Who Let the Dogs Out'?" as a good example with comment "This is awesome!"
    Then I should see a message saying the feedback was sent successfully
    And the admin should see "This is awesome!" in the generated training data

  @ignore
  Scenario: Admin should not be see questions with bad feedback and comment in generated training data
    When I suggest the displayed question "Who wrote 'Who Let the Dogs Out'?" as a bad example with comment "This is terrible!"
    Then I should see a message saying the feedback was sent successfully
    And the admin should not see "This is terrible!" in the generated training data

  @ignore
  Scenario: User should not be able to submit response without a specific feedback
    When I suggest the displayed question "Who wrote 'Who Let the Dogs Out'?" without feedback
    Then I should see a message saying the feedback was rejected
    And the admin should not see empty feedback in the generated training data

  @ignore
  Scenario: User should not be able to submit response again for the same question
    When I suggest the displayed question "Who wrote 'Who Let the Dogs Out'?" with an existing feedback
    Then I should see a message saying the feedback was rejected
    And the admin should not see duplicate feedback in the generated training data

  Scenario: Admin edit the first question and choice suggested
    Given I suggest the displayed question "Who wrote 'Who Let the Dogs Out'?" as a good example
    When an admin edit the question and choices "Who wrote 'Who Let the Dogs Out'?" with a different question:
      | Question Stem                              | Choice A |
      | Did Baha Men write 'Who Let the Dogs Out'? | Yes      |
    Then an admin should be able to download the training data containing 1 example containing "Did Baha Men write 'Who Let the Dogs Out'?"
    And an admin should be able to download the training data containing 1 example containing "Yes"
