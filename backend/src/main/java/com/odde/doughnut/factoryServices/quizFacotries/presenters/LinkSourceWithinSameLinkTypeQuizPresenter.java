package com.odde.doughnut.factoryServices.quizFacotries.presenters;

import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.QuizQuestionEntity;
import com.odde.doughnut.entities.Thing;
import com.odde.doughnut.entities.json.QuizQuestion;
import java.util.List;
import java.util.stream.Stream;

public class LinkSourceWithinSameLinkTypeQuizPresenter extends QuizQuestionWithOptionsPresenter {
  protected final Link link;

  public LinkSourceWithinSameLinkTypeQuizPresenter(QuizQuestionEntity quizQuestion) {
    super(quizQuestion);
    this.link = quizQuestion.getThing().getLink();
  }

  @Override
  public String mainTopic() {
    return link.getTargetNote().getTitle();
  }

  @Override
  public String stem() {
    return "Which one <em>is immediately " + link.getLinkTypeLabel() + "</em>:";
  }

  @Override
  protected List<QuizQuestion.Choice> getOptionsFromThings(Stream<Thing> noteStream) {
    return noteStream
        .map(
            thing -> {
              QuizQuestion.Choice choice = new QuizQuestion.Choice();
              choice.setDisplay(thing.getLink().getClozeSource().cloze());
              return choice;
            })
        .toList();
  }
}