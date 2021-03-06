package com.lockotron.mobi_o_tron;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lockotron.mobi_o_tron.exception.ServerNotSetException;
import com.lockotron.mobi_o_tron.controller.Historico;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MOBI-O-TRON";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Snackbar serverNotSetSnackbar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefresh;
    private Snackbar serverErrorSnackbar;
    private List<com.lockotron.mobi_o_tron.model.Historico> mList = new ArrayList<>();

    public LogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogFragment newInstance(String param1, String param2) {
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.log_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new LogAdapter(mList));

        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.accent);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLog(getContext());
            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // snackbar que mostra erro caso servidor não tenha sido definido
        serverNotSetSnackbar = Snackbar.make(getView(), ServerNotSetException.PUBLIC_ERROR_MESSAGE, Snackbar.LENGTH_INDEFINITE);
        serverNotSetSnackbar.setAction(R.string.snackbar_action_settings, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
                intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                startActivity(intent);
            }
        });

        serverErrorSnackbar = Snackbar.make(getView(), R.string.error_server_generic, Snackbar.LENGTH_LONG);

        refreshLog(getContext());
    }

    private void refreshLog(Context context) {
        GetLogTask getLogTask = new GetLogTask();
        getLogTask.execute(context);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    class GetLogTask extends AsyncTask<Context, Void, Void> {

        @Override
        protected void onPreExecute() {
            mSwipeRefresh.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            try {
                mList.clear();
                mList.addAll(Historico.getAll(contexts[0]));
            } catch (IOException e) {
                serverErrorSnackbar.show();
                e.printStackTrace();
            } catch (ServerNotSetException e) {
                serverNotSetSnackbar.show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            mRecyclerView.getAdapter().notifyDataSetChanged();
            mSwipeRefresh.setRefreshing(false);
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {
        private List<com.lockotron.mobi_o_tron.model.Historico> mHistoricos;

        public LogAdapter(List<com.lockotron.mobi_o_tron.model.Historico> historicos) {
            this.mHistoricos = historicos;
        }

        @Override
        public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_log, parent, false);
            return new LogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LogViewHolder holder, int position) {
            com.lockotron.mobi_o_tron.model.Historico historico = mHistoricos.get(position);
            holder.titleView.setText(historico.getUsuario().getNome());
            holder.dateView.setText(historico.getData());
            final Drawable icon;
            final int iconRes, colorRes, color, statusRes;
            if (historico.getEstado()) {
                iconRes = R.drawable.ic_check_circle;
                colorRes = R.color.green;
                statusRes = R.string.log_access_granted;
            } else {
                iconRes = R.drawable.ic_remove_circle;
                colorRes = R.color.red;
                statusRes = R.string.log_access_denied;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                icon = getResources().getDrawable(iconRes, getActivity().getTheme());
            else icon = getResources().getDrawable(iconRes);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                color = getResources().getColor(colorRes, getActivity().getTheme());
            else color = getResources().getColor(colorRes);

            assert icon != null;
            DrawableCompat.wrap(icon);
            DrawableCompat.setTint(icon, color);

            holder.badgeView.setImageDrawable(icon);
            holder.badgeView.setContentDescription(getResources().getString(statusRes));
        }

        @Override
        public int getItemCount() {
            return mHistoricos.size();
        }
    }

    private class LogViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView dateView;
        public ImageView badgeView;
        public LogViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.title);
            dateView = (TextView) itemView.findViewById(R.id.date_text);
            badgeView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
