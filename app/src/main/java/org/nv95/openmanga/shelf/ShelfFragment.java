package org.nv95.openmanga.shelf;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.nv95.openmanga.R;
import org.nv95.openmanga.AppBaseFragment;
import org.nv95.openmanga.common.Dismissible;
import org.nv95.openmanga.common.utils.ResourceUtils;

/**
 * Created by koitharu on 21.12.17.
 */

public final class ShelfFragment extends AppBaseFragment implements LoaderManager.LoaderCallbacks<ShelfContent> {

	private RecyclerView mRecyclerView;
	private ShelfAdapter mAdapter;
	private int mColumnCount;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mColumnCount = 12;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, R.layout.recyclerview);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView = view.findViewById(R.id.recyclerView);
		mRecyclerView.setHasFixedSize(true);
		new ItemTouchHelper(new DismissCallback()).attachToRecyclerView(mRecyclerView);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (ResourceUtils.isLandscapeTablet(getResources())) {
			mColumnCount = 12;
		}
		mAdapter = new ShelfAdapter((OnTipsActionListener) getActivity());
		GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), mColumnCount);
		layoutManager.setSpanSizeLookup(new ShelfSpanSizeLookup(mAdapter, mColumnCount));
		mRecyclerView.addItemDecoration(new ShelfItemSpaceDecoration(ResourceUtils.dpToPx(getResources(), 4), mAdapter, mColumnCount));
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
		getLoaderManager().getLoader(0).forceLoad(); //TODO
	}

	@Override
	public Loader<ShelfContent> onCreateLoader(int i, Bundle bundle) {
		return new ShelfLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<ShelfContent> loader, ShelfContent content) {
		ShelfUpdater.update(mAdapter, content, mColumnCount);
	}

	@Override
	public void onLoaderReset(Loader<ShelfContent> loader) {

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.options_shelf, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_shelf_settings:
				//TODO
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void scrollToTop() {
		mRecyclerView.smoothScrollToPosition(0);
	}

	private class DismissCallback extends ItemTouchHelper.SimpleCallback {

		DismissCallback() {
			super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
		}

		@Override
		public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
			return viewHolder instanceof ShelfAdapter.TipHolder && ((ShelfAdapter.TipHolder) viewHolder).isDismissible() ?
					super.getSwipeDirs(recyclerView, viewHolder) : 0;
		}

		@Override
		public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
			return false;
		}

		@Override
		public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
			if (viewHolder instanceof Dismissible) {
				((Dismissible) viewHolder).dismiss();
			}
		}
	}
}