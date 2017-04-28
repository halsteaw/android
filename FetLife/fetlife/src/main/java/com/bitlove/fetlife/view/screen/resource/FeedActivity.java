package com.bitlove.fetlife.view.screen.resource;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.UrlUtil;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.feed.FeedRecyclerAdapter;
import com.bitlove.fetlife.view.screen.component.MenuActivityComponent;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;

public class FeedActivity extends ResourceListActivity<Story> implements MenuActivityComponent.MenuActivityCallBack, FeedRecyclerAdapter.OnFeedItemClickListener {

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, FeedActivity.class);
        if (FetLifeApplication.getInstance().getUserSessionManager().getActiveUserPreferences().getBoolean(context.getString(R.string.settings_key_general_feed_as_start),false)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return intent;
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        super.onResourceCreate(savedInstanceState);
    }

    @Override
    protected ResourceListRecyclerAdapter<Story, ?> createRecyclerAdapter(Bundle savedInstanceState) {
        return new FeedRecyclerAdapter(getFetLifeApplication(), this, null);
    }

    @Override
    protected String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_FEED;
    }

    @Override
    public void onItemClick(Story feedStory) {
    }

    @Override
    public void onAvatarClick(Story feedStory) {
    }

    @Override
    public void onMemberClick(Member member) {
        //TODO(feed): Remove this mergeSave after Feed is stored in local db
        member.mergeSave();
        ProfileActivity.startActivity(this, member.getId());
    }

    @Override
    public void onFeedInnerItemClick(Story.FeedStoryType feedStoryType, String url, Member targetMember) {
        if (targetMember != null) {
            if (feedStoryType == Story.FeedStoryType.FOLLOW_CREATED || feedStoryType == Story.FeedStoryType.FRIEND_CREATED) {
                //TODO(feed): Remove this mergeSave after Feed is stored in local db
                targetMember.mergeSave();
                ProfileActivity.startActivity(this, targetMember.getId());
                return;
            }
        }
        UrlUtil.openUrl(this,url);
    }

    @Override
    public void onFeedImageClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {
        if (targetMember != null) {
            if (feedStoryType == Story.FeedStoryType.FOLLOW_CREATED || feedStoryType == Story.FeedStoryType.FRIEND_CREATED) {
                //TODO(feed): Remove this mergeSave after Feed is stored in local db
                targetMember.mergeSave();
                ProfileActivity.startActivity(this, targetMember.getId());
                return;
            }
        }
        if (feedStoryType == Story.FeedStoryType.LIKE_CREATED && feedEvent.getSecondaryTarget().getVideo() != null) {
            Uri uri = Uri.parse(feedEvent.getSecondaryTarget().getVideo().getVideoUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
            return;
        }
        UrlUtil.openUrl(this,url);
    }

    @Override
    public void onFeedImageLongClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {
        UrlUtil.openUrl(this,url);
    }

    @Override
    public void onVisitItem(Object object, String url) {
        UrlUtil.openUrl(this,url);
    }

    @Override
    public boolean finishAtMenuNavigation() {
        return !getFetLifeApplication().getUserSessionManager().getActiveUserPreferences().getBoolean(getString(R.string.settings_key_general_feed_as_start),false);
    }
}
