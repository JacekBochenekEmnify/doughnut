package com.odde.doughnut.controllers;

import com.odde.doughnut.controllers.dto.NoteCreationDTO;
import com.odde.doughnut.controllers.dto.NotebooksViewedByUser;
import com.odde.doughnut.controllers.dto.RedirectToNoteResponse;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.Notebook;
import com.odde.doughnut.entities.NotebookSettings;
import com.odde.doughnut.entities.User;
import com.odde.doughnut.exceptions.UnexpectedNoAccessRightException;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.models.BazaarModel;
import com.odde.doughnut.models.JsonViewer;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.TestabilitySettings;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notebooks")
class RestNotebookController {
  private final ModelFactoryService modelFactoryService;
  private UserModel currentUser;

  @Resource(name = "testabilitySettings")
  private final TestabilitySettings testabilitySettings;

  public RestNotebookController(
      ModelFactoryService modelFactoryService,
      UserModel currentUser,
      TestabilitySettings testabilitySettings) {
    this.modelFactoryService = modelFactoryService;
    this.currentUser = currentUser;
    this.testabilitySettings = testabilitySettings;
  }

  @GetMapping("")
  public NotebooksViewedByUser myNotebooks() {
    currentUser.assertLoggedIn();

    User user = currentUser.getEntity();
    NotebooksViewedByUser notebooksViewedByUser =
        new JsonViewer().jsonNotebooksViewedByUser(user.getOwnership().getNotebooks());
    notebooksViewedByUser.subscriptions = user.getSubscriptions();
    return notebooksViewedByUser;
  }

  @PostMapping({"/create"})
  @Transactional
  public RedirectToNoteResponse createNotebook(@Valid @RequestBody NoteCreationDTO noteCreation) {
    currentUser.assertLoggedIn();
    User userEntity = currentUser.getEntity();
    Note note =
        userEntity
            .getOwnership()
            .createAndPersistNotebook(
                userEntity, testabilitySettings.getCurrentUTCTimestamp(),
                modelFactoryService, noteCreation.getTopicConstructor());
    return new RedirectToNoteResponse(note.getId());
  }

  @PostMapping(value = "/{notebook}")
  @Transactional
  public Notebook update(
      @PathVariable @Schema(type = "integer") Notebook notebook,
      @Valid @RequestBody NotebookSettings notebookSettings)
      throws UnexpectedNoAccessRightException {
    currentUser.assertAuthorization(notebook);
    notebook.getNotebookSettings().update(notebookSettings);
    modelFactoryService.save(notebook);
    return notebook;
  }

  @PostMapping(value = "/{notebook}/share")
  @Transactional
  public Notebook shareNotebook(
      @PathVariable("notebook") @Schema(type = "integer") Notebook notebook)
      throws UnexpectedNoAccessRightException {
    currentUser.assertAuthorization(notebook);
    BazaarModel bazaar = modelFactoryService.toBazaarModel();
    bazaar.shareNotebook(notebook);
    return notebook;
  }
}
