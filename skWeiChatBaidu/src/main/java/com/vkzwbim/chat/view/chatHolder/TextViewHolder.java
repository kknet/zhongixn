package com.vkzwbim.chat.view.chatHolder;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.vkzwbim.chat.R;
import com.vkzwbim.chat.bean.message.ChatMessage;
import com.vkzwbim.chat.util.Constants;
import com.vkzwbim.chat.util.HtmlUtils;
import com.vkzwbim.chat.util.PreferenceUtils;
import com.vkzwbim.chat.util.StringUtils;
import com.vkzwbim.chat.util.link.HttpTextView;

public class TextViewHolder extends AChatHolderInterface {

    public HttpTextView mTvContent;
    public TextView tvFireTime;

    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_text : R.layout.chat_to_item_text;
    }

    @Override
    public void initView(View view) {
        mTvContent = view.findViewById(R.id.chat_text);
        mRootView = view.findViewById(R.id.chat_warp_view);
        if (!isMysend) {
            tvFireTime = view.findViewById(R.id.tv_fire_time);
        }
    }

    @Override
    public void fillData(ChatMessage message) {
        // 修改字体功能
        int size = PreferenceUtils.getInt(mContext, Constants.FONT_SIZE) + 16;
        mTvContent.setTextSize(size);
        mTvContent.setTextColor(mContext.getResources().getColor(R.color.black));

        String content = StringUtils.replaceSpecialChar(message.getContent());
        CharSequence charSequence = HtmlUtils.transform200SpanString(content, true);
        Log.e("zx", "fillData: " + charSequence.toString() + "  length: " + charSequence.length() + " split: " + charSequence.toString().split("\\[").length);
        if (message.getIsReadDel() && !isMysend) {// 阅后即焚
            if (!message.isGroup() && !message.isSendRead()) {
                mTvContent.setText(R.string.tip_click_to_read);
                mTvContent.setTextColor(mContext.getResources().getColor(R.color.redpacket_bg));
            } else {
                // 已经查看了，当适配器再次刷新的时候，不需要重新赋值
                mTvContent.setText(charSequence);
            }
        } else {
            mTvContent.setText(charSequence);
        }

        mTvContent.setUrlText(mTvContent.getText());

        mTvContent.setOnClickListener(v -> mHolderListener.onItemClick(mRootView, TextViewHolder.this, mdata));
        mTvContent.setOnLongClickListener(v -> {
            mHolderListener.onItemLongClick(v, TextViewHolder.this, mdata);
            return true;
        });
    }

    @Override
    protected void onRootClick(View v) {

    }

    @Override
    public boolean enableFire() {
        return true;
    }

    @Override
    public boolean enableSendRead() {
        return true;
    }

    public void showFireTime(boolean show) {
        if (tvFireTime != null) {
            tvFireTime.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}