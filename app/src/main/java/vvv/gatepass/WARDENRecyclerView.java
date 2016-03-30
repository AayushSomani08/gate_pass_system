package vvv.gatepass;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pradumn K Mahanta on 29-03-2016.
 */
public class WARDENRecyclerView extends RecyclerView.Adapter<WARDENRecyclerView.ViewHolder>{

    private final List<GatepassListViewItem> mValues;
    private final WARDENGatepassFragment.OnWARDENGatepassListFragmentInteractionListener mListener;

    public WARDENRecyclerView(List<GatepassListViewItem> items,  WARDENGatepassFragment.OnWARDENGatepassListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gatepass_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.gp_StudentName.setText(holder.mItem.gp_StudentName);
        holder.gp_GatepassNumber.setText(holder.mItem.gp_GatepassNumber);
        holder.inTime.setText(holder.mItem.gp_InTime);
        holder.outTime.setText(holder.mItem.gp_OutTime);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.onWARDENGatepassListFragmentInteraction(view.getContext(),holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView gp_StudentName;
        public final TextView inTime;
        public final TextView outTime;
        public final TextView gp_GatepassNumber;
        public GatepassListViewItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            gp_StudentName = (TextView) view.findViewById(R.id.gp_FullName);
            gp_GatepassNumber = (TextView) view.findViewById(R.id.gp_Number);
            inTime = (TextView) view.findViewById(R.id.inTime);
            outTime = (TextView) view.findViewById(R.id.outTIme);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + gp_GatepassNumber.getText() + "'";
        }
    }
}
