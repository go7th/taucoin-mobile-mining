package io.taucoin.android.wallet.module.view.main;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.taucoin.android.wallet.R;
import io.taucoin.android.wallet.base.TransmitKey;
import io.taucoin.android.wallet.db.entity.MiningReward;
import io.taucoin.android.wallet.util.ActivityUtil;
import io.taucoin.android.wallet.util.CopyManager;
import io.taucoin.android.wallet.util.FmtMicrometer;
import io.taucoin.android.wallet.util.ToastUtils;
import io.taucoin.foundation.util.DimensionsUtil;
import io.taucoin.foundation.util.StringUtil;

public class MiningRewardAdapter extends BaseAdapter {

    private List<MiningReward> list = new ArrayList<>();

    void setListData(List<MiningReward> data, boolean isAdd) {
        if (!isAdd) {
           list.clear();
        }
        if (data != null) {
            list.addAll(data);
        }
        notifyDataSetChanged();
    }

    String getTxHash(int pos) {
        if(pos < 0 || pos > getCount()){
            return "";
        }
        return list.get(pos).getTxHash();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MiningReward bean = list.get(position);
        viewHolder.tvHash.setText(bean.getTxHash());
        long fee = bean.getMinerFee() + bean.getPartFee();
        String feeStr = String.valueOf(fee);
        viewHolder.tvFee.setText(FmtMicrometer.fmtFormat(feeStr));
        int reStatus = R.string.home_mining_miner_participant;
        if(bean.getMinerFee() == 0){
            reStatus = R.string.home_mining_participant;
        }else if(bean.getPartFee() == 0){
            reStatus = R.string.home_mining_miner;
        }
        viewHolder.tvStatus.setText(reStatus);

        convertView.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP && clickDownTime > 0){
                long currentTime = new Date().getTime();
                long delayTime = currentTime - clickDownTime;
                if(delayTime < 300){
                    String txId = getTxHash(position);
                    String tauExplorerTxUr = TransmitKey.ExternalUrl.TAU_EXPLORER_TX_URL;
                    tauExplorerTxUr += txId;
                    ActivityUtil.openUri(v.getContext(), tauExplorerTxUr);
                }else{
                    int tvHashWidth = viewHolder.tvHash.getWidth() + DimensionsUtil.dip2px(v.getContext(), 15);
                    if(tvHashWidth >= event.getX()){
                        if(StringUtil.isNotEmpty(bean.getTxHash())){
                            CopyManager.copyText(bean.getTxHash());
                            ToastUtils.showShortToast(R.string.tx_hash_copy);
                        }
                    }
                }
                clickDownTime = 0;
            }else if(event.getAction() == MotionEvent.ACTION_OUTSIDE ||
                    event.getAction() == MotionEvent.ACTION_CANCEL){
                clickDownTime = 0;
            }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                clickDownTime = new Date().getTime();
            }
            return true;
        });
        return convertView;
    }

    private long clickDownTime = 0;

    class ViewHolder {
        @BindView(R.id.tv_hash)
        TextView tvHash;
        @BindView(R.id.tv_fee)
        TextView tvFee;
        @BindView(R.id.tv_status)
        TextView tvStatus;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
