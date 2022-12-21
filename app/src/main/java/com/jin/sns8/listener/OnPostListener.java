package com.jin.sns8.listener;

import com.jin.sns8.PostInfo;

public interface OnPostListener {
    void onDelete(PostInfo postInfo);
    void onModify();
}
