// ***********************************************
// custom commands and overwrite existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

/// <reference types="cypress" />
// @ts-check
import { DefaultPredicate, FlexiPredicate, Operator } from "@anev/ts-mountebank"
import "@testing-library/cypress/add-commands"
import "cypress-file-upload"
import "./string.extensions"
import ServiceMocker from "./ServiceMocker"
import { HttpMethod, Predicate } from "@anev/ts-mountebank"

type FunctionCall = {
  role: "function"
  function_call: {
    name: string
    arguments: string
  }
}

type TextBasedMessage = {
  role: "user" | "assistant"
  content: string
}

type ChatMessage = TextBasedMessage | FunctionCall

type MessageToMatch = {
  role: "user" | "assistant"
  content: string | RegExp
}

function mockChatCompletion(
  predicate: Predicate,
  serviceMocker: ServiceMocker,
  message: ChatMessage,
  finishReason: "length" | "stop" | "function_call",
) {
  return serviceMocker.mockWithPredicate(predicate, {
    object: "chat.completion",
    created: 1589478378,
    choices: [
      {
        message,
        index: 0,
        finish_reason: finishReason,
      },
    ],
    usage: {
      prompt_tokens: 5,
      completion_tokens: 7,
      total_tokens: 12,
    },
  })
}

function mockChatCompletionAsAssistant(
  predicate: Predicate,
  serviceMocker: ServiceMocker,
  reply: string,
  finishReason: "length" | "stop",
) {
  return mockChatCompletion(
    predicate,
    serviceMocker,
    { role: "assistant", content: reply },
    finishReason,
  )
}

function mockChatCompletionWithBody(
  serviceMocker: ServiceMocker,
  messagesToMatch: MessageToMatch[],
  reply: string,
  finishReason: "length" | "stop",
) {
  const body = { messages: messagesToMatch }
  const predicate = new FlexiPredicate()
    .withOperator(Operator.matches)
    .withPath(`/v1/chat/completions`)
    .withMethod(HttpMethod.POST)
    .withBody(body)
  return mockChatCompletionAsAssistant(predicate, serviceMocker, reply, finishReason)
}

Cypress.Commands.add(
  "mockChatCompletion",
  { prevSubject: true },
  (serviceMocker: ServiceMocker, prompt: string, reply: string) => {
    const messages = [
      { role: "user", content: new RegExp("^" + Cypress._.escapeRegExp(prompt) + "$") },
    ]
    return mockChatCompletionWithBody(serviceMocker, messages, reply, "stop")
  },
)

Cypress.Commands.add(
  "mockChatCompletionWithIncompleteAssistantMessage",
  { prevSubject: true },
  (
    serviceMocker: ServiceMocker,
    incomplete: string,
    reply: string,
    finishReason: "stop" | "length",
  ) => {
    const body = {
      messages: [{ content: "^" + Cypress._.escapeRegExp(incomplete) + "$" }],
    }
    const predicate = new FlexiPredicate()
      .withOperator(Operator.matches)
      .withPath(`/v1/chat/completions`)
      .withMethod(HttpMethod.POST)
      .withBody(body)
    return mockChatCompletionAsAssistant(predicate, serviceMocker, reply, finishReason)
  },
)

Cypress.Commands.add(
  "mockChatCompletionWithContext",
  { prevSubject: true },
  (serviceMocker: ServiceMocker, reply: string, context: string) => {
    const body = { messages: [{ role: "system", content: context }] }
    const predicate = new FlexiPredicate()
      .withOperator(Operator.matches)
      .withPath(`/v1/chat/completions`)
      .withMethod(HttpMethod.POST)
      .withBody(body)
    return mockChatCompletionAsAssistant(predicate, serviceMocker, reply, "stop")
  },
)

Cypress.Commands.add("restartImposter", { prevSubject: true }, (serviceMocker: ServiceMocker) => {
  return serviceMocker.install()
})

Cypress.Commands.add(
  "stubChatCompletion",
  { prevSubject: true },
  (serviceMocker: ServiceMocker, reply: string, finishReason: "length" | "stop") => {
    const predicate = new DefaultPredicate(`/v1/chat/completions`, HttpMethod.POST)
    return mockChatCompletionAsAssistant(predicate, serviceMocker, reply, finishReason)
  },
)

Cypress.Commands.add(
  "stubChatCompletionFunctionCall",
  { prevSubject: true },
  (
    serviceMocker: ServiceMocker,
    functionName: string,
    argumentsString: string,
    bodyContains: string | null = null,
  ) => {
    let predicate
    if (bodyContains === null) {
      predicate = new DefaultPredicate(`/v1/chat/completions`, HttpMethod.POST)
    } else {
      predicate = new FlexiPredicate()
        .withMethod(HttpMethod.POST)
        .withPath("v1/chat/completions")
        .withOperator(Operator.contains)
        .withBody(bodyContains)
    }

    return mockChatCompletion(
      predicate,
      serviceMocker,
      {
        role: "function",
        function_call: {
          name: functionName,
          arguments: argumentsString,
        },
      },
      "function_call",
    )
  },
)

Cypress.Commands.add("stubCreateImage", { prevSubject: true }, (serviceMocker: ServiceMocker) => {
  return serviceMocker.stubPoster(`/v1/images/generations`, {
    created: 1589478378,
    data: [
      {
        url: "https://moon",
        b64_json:
          "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
      },
    ],
  })
})

Cypress.Commands.add(
  "stubOpenAiCompletionWithErrorResponse",
  { prevSubject: true },
  (serviceMocker: ServiceMocker) => {
    return serviceMocker.stubGetterWithError500Response(`/*`, {})
  },
)

Cypress.Commands.add(
  "alwaysResponseAsUnauthorized",
  { prevSubject: true },
  async (serviceMocker: ServiceMocker) => {
    await serviceMocker.install()
    await serviceMocker.stubPosterUnauthorized(`/*`, {
      status: "BAD_REQUEST",
      message: "nah nah nah, you need a valid token",
      errors: {
        "OpenAi Error": "BAD_REQUEST",
      },
    })
  },
)
