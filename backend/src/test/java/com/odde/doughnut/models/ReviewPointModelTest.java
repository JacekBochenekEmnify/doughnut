package com.odde.doughnut.models;

import com.odde.doughnut.entities.NoteEntity;
import com.odde.doughnut.testability.MakeMe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:repository.xml"})
@Transactional
public class ReviewPointModelTest {
    @Autowired
    MakeMe makeMe;
    UserModel userModel;
    Timestamp day1;

    @BeforeEach
    void setup() {
        userModel = makeMe.aUser().toModelPlease();
        day1 = makeMe.aTimestamp().of(1, 8).forWhereTheUserIs(userModel).please();
    }

    @Nested
    class InitialReview {

        @Test
        void initialReviewShouldSetBothInitialAndLastReviewAt() {
            NoteEntity noteEntity = makeMe.aNote().forUser(userModel).please();
            ReviewPointModel reviewPoint = makeMe.aReviewPointFor(noteEntity).by(userModel).toModelPlease();
            reviewPoint.initalReview(userModel, day1);
            assertThat(reviewPoint.getEntity().getInitialReviewedAt(), equalTo(day1));
            assertThat(reviewPoint.getEntity().getLastReviewedAt(), equalTo(day1));
        }
    }
}
