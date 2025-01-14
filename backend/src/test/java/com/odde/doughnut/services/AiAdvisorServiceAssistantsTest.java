package com.odde.doughnut.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.odde.doughnut.testability.OpenAIChatCompletionMock;
import com.odde.doughnut.testability.model.MemorySettingAccessor;
import com.theokanning.openai.assistants.assistant.Assistant;
import com.theokanning.openai.assistants.assistant.AssistantRequest;
import com.theokanning.openai.assistants.assistant.Tool;
import com.theokanning.openai.client.OpenAiApi;
import io.reactivex.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class AiAdvisorServiceAssistantsTest {

  private AiAdvisorService aiAdvisorService;
  @Mock private OpenAiApi openAiApi;
  OpenAIChatCompletionMock openAIChatCompletionMock;

  @BeforeEach
  void Setup() {
    MockitoAnnotations.openMocks(this);
    openAIChatCompletionMock = new OpenAIChatCompletionMock(openAiApi);
    aiAdvisorService = new AiAdvisorService(openAiApi);
  }

  @Nested
  class CreateAssistants {
    AssistantRequest assistantRequest;

    @BeforeEach
    void captureTheRequest() {
      when(openAiApi.createAssistant(ArgumentMatchers.any()))
          .thenReturn(Single.just(new Assistant()));
      SettingAccessor settingAccessor = new MemorySettingAccessor("example-id");
      aiAdvisorService.getContentCompletionService(settingAccessor).createAssistant("gpt4o", null);
      ArgumentCaptor<AssistantRequest> captor = ArgumentCaptor.forClass(AssistantRequest.class);
      verify(openAiApi).createAssistant(captor.capture());
      assistantRequest = captor.getValue();
    }

    @Test
    void getAiSuggestion_givenAString_returnsAiSuggestionObject() {
      assertThat(assistantRequest.getName(), is("Note details completion"));
      assertThat(assistantRequest.getInstructions(), containsString("PKM system"));
      assertThat(assistantRequest.getTools(), hasSize(2));
    }

    @Test
    void parameters() {
      Tool tool = assistantRequest.getTools().get(0);
      assertThat(tool.getType(), is("function"));
    }
  }
}
