package com.odde.doughnut.factoryServices.quizFacotries.presenters;

import com.odde.doughnut.entities.*;

public class FromSamePartAsQuizPresenter extends QuizQuestionWithOptionsPresenter {
  protected final Thing link;
  private final Thing categoryLink;

  public FromSamePartAsQuizPresenter(QuizQuestionEntity quizQuestion) {
    super(quizQuestion);
    this.link = quizQuestion.getThing();
    this.categoryLink = quizQuestion.getCategoryLink();
  }

  @Override
  public String mainTopic() {
    return link.getSourceNote().getTopicConstructor();
  }

  @Override
  public String stem() {
    return "<p>Which one <mark>is "
        + link.getLinkTypeLabel()
        + "</mark> the same "
        + categoryLink.getLinkType().nameOfSource
        + " of <mark>"
        + categoryLink.getTargetNote().getTopicConstructor()
        + "</mark> as:";
  }
}
