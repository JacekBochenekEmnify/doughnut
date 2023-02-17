package com.odde.doughnut.controllers;

import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.json.AiEngagingStory;
import com.odde.doughnut.entities.json.AiSuggestion;
import com.odde.doughnut.entities.json.AiSuggestionRequest;
import com.odde.doughnut.exceptions.UnexpectedNoAccessRightException;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.services.AiAdvisorService;
import com.theokanning.openai.OpenAiApi;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;

@RestController
@SessionScope
@RequestMapping("/api/ai")
public class RestAiController {
  AiAdvisorService aiAdvisorService;
  private UserModel currentUser;

  public RestAiController(
      @Qualifier("testableOpenAiApi") OpenAiApi openAiApi, UserModel currentUser) {
    aiAdvisorService = new AiAdvisorService(openAiApi);
    this.currentUser = currentUser;
  }

  @PostMapping(value = "/ask-suggestions" /*, produces = MediaType.APPLICATION_NDJSON_VALUE*/)
  public AiSuggestion askSuggestion(@RequestBody AiSuggestionRequest aiSuggestionRequest) {
    currentUser.assertLoggedIn();
    return aiAdvisorService.getAiSuggestion(aiSuggestionRequest.prompt).blockFirst();
  }

  @GetMapping("/ask-engaging-stories")
  public AiEngagingStory askEngagingStories(@RequestParam List<Note> notes)
      throws UnexpectedNoAccessRightException {
    currentUser.assertReadAuthorization(notes);
    List<String> titles = notes.stream().map(Note::getTitle).toList();
    return aiAdvisorService.getEngagingStory(titles);
  }
}
