/*
 * Copyright 2016 Freelander
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jun.elephant.ui.topic.publish;

import com.jun.elephant.entity.topic.CategoryEntity;
import com.jun.elephant.entity.topic.TopicPublishEntity;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by Jun on 2016/10/19.
 */
public class TopicPublishPresenter extends TopicPublishContract.Presenter {

    private Observer<CategoryEntity> mCategoryObserver = new Observer<CategoryEntity>() {
        @Override
        public void onCompleted() {
            mView.onRequestEnd();
        }

        @Override
        public void onError(Throwable e) {
            mView.onRequestError(e.toString());
            mView.onInternetError();
        }

        @Override
        public void onNext(CategoryEntity categoryEntity) {
            mView.getCategory(categoryEntity);
        }
    };

    private Observer<TopicPublishEntity> mTopicDetailObserver = new Observer<TopicPublishEntity>() {
        @Override
        public void onCompleted() {
            mView.onRequestEnd();
        }

        @Override
        public void onError(Throwable e) {
            mView.onRequestError(e.toString());
            mView.onInternetError();
        }

        @Override
        public void onNext(TopicPublishEntity topicPublishEntity) {
            mView.publishTopicSuccess(topicPublishEntity.getData());
        }
    };

    @Override
    public void getCategory() {
        mRxManager.add(mModel.getCategories().subscribe(mCategoryObserver));
    }

    @Override
    public void publishTopic(String title, String body, String categoryId) {
        mRxManager.add(mModel.publishTopic(title, body, categoryId)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.onRequestStart();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(mTopicDetailObserver));
    }
}
