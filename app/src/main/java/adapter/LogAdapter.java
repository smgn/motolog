package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
	@Override
	public LogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return null;
	}

	@Override
	public void onBindViewHolder(LogAdapter.ViewHolder holder, int position) {

	}

	@Override
	public int getItemCount() {
		return 0;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View itemView) {
			super(itemView);
		}
	}
}
