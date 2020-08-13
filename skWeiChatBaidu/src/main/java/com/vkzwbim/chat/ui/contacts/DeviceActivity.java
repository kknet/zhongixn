package com.vkzwbim.chat.ui.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vkzwbim.chat.MyApplication;
import com.vkzwbim.chat.R;
import com.vkzwbim.chat.bean.EventLoginStatus;
import com.vkzwbim.chat.bean.Friend;
import com.vkzwbim.chat.db.dao.FriendDao;
import com.vkzwbim.chat.db.dao.login.MachineDao;
import com.vkzwbim.chat.helper.AvatarHelper;
import com.vkzwbim.chat.ui.base.BaseActivity;
import com.vkzwbim.chat.ui.message.ChatActivity;
import com.vkzwbim.chat.util.CommonAdapter;
import com.vkzwbim.chat.util.CommonViewHolder;
import com.vkzwbim.chat.util.EventBusHelper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class DeviceActivity extends BaseActivity {
    private ListView mDeviceList;
    private DeviceAdapter mDeviceAdapter;
    private List<Friend> mDeviceData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initActionBar();
        loadData();
        initView();
        EventBusHelper.register(this);
    }

    private void loadData() {
        mDeviceData = new ArrayList<>();
        if (MyApplication.IS_SUPPORT_MULTI_LOGIN) {
            mDeviceData = FriendDao.getInstance().getDevice(coreManager.getSelf().getUserId());
        }
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(R.string.my_device);
    }

    private void initView() {
        mDeviceList = (ListView) findViewById(R.id.device_list);
        mDeviceAdapter = new DeviceAdapter(this, mDeviceData);
        mDeviceList.setAdapter(mDeviceAdapter);
        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DeviceActivity.this, ChatActivity.class);
                intent.putExtra(ChatActivity.FRIEND, mDeviceData.get(position));
                startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(final EventLoginStatus message) {
        mDeviceAdapter.notifyDataSetChanged();
    }

    class DeviceAdapter extends CommonAdapter<Friend> {

        DeviceAdapter(Context context, List<Friend> data) {
            super(context, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CommonViewHolder viewHolder = CommonViewHolder.get(mContext, convertView, parent,
                    R.layout.row_device, position);

            ImageView iv = viewHolder.getView(R.id.device_ava);
            TextView tv = viewHolder.getView(R.id.device_name);
            Friend friend = data.get(position);
            if (friend != null) {
                AvatarHelper.getInstance().displayAvatar(friend.getUserId(), iv);
                String str;
                if (MachineDao.getInstance().getMachineOnLineStatus(friend.getUserId())) {
                    str = getString(R.string.status_online);
                } else {
                    str = getString(R.string.status_offline);
                }
                tv.setText(friend.getNickName() + str);
            }

            return viewHolder.getConvertView();
        }
    }
}