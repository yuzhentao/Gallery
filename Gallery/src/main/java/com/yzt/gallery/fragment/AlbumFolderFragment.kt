package com.yzt.gallery.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.gyf.immersionbar.ImmersionBar
import com.yzt.gallery.R
import com.yzt.gallery.activity.AlbumActivity
import com.yzt.gallery.adapter.AlbumFolderAdapter
import com.yzt.gallery.adapter.AlbumFolderAdapterNew
import com.yzt.gallery.bean.AlbumFolder
import com.yzt.gallery.rx.AlbumRxSchedulers
import com.yzt.gallery.util.*
import com.yzt.gallery.viewModel.AlbumViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * 相册文件夹
 *
 * @author yzt 2020/4/22
 */
class AlbumFolderFragment : Fragment() {

    private var top: ConstraintLayout? = null
    private var vLeft: View? = null
    private var ivLeft: AppCompatImageView? = null
    private var tvCenter: AppCompatTextView? = null
    private var rv: RecyclerView? = null

    private val layoutManager by lazy {
        AlbumLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
//    private var adapter: AlbumFolderAdapter? = null
    private var adapter: AlbumFolderAdapterNew? = null

    private var onClickListener: View.OnClickListener? = null

    private val compositeDisposable = CompositeDisposable()

    private var viewModel: AlbumViewModel? = null

    private var lastPosition = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_album_folder, container, false)
        top = view.findViewById(R.id.top)
        vLeft = view.findViewById(R.id.v_top_left)
        ivLeft = view.findViewById(R.id.iv_top_left)
        tvCenter = view.findViewById(R.id.tv_top_center)
        rv = view.findViewById(R.id.rv)
        AlbumViewUtil.setPadding(top, 0, ImmersionBar.getStatusBarHeight(this), 0, 0)
        AlbumViewUtil.gone(tvCenter)
        onClickListener?.let {
            vLeft!!.setOnClickListener(it)
        }
        ivLeft!!.setImageResource(R.drawable.ic_album_close_green)
        AlbumRecyclerViewUtil.config(layoutManager, rv)
        val itemDecoration = AlbumLinearItemDecoration(AlbumDimenUtil.dp2px(context,2), false)
        rv!!.addItemDecoration(itemDecoration)
        (rv!!.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
//        adapter = AlbumFolderAdapter(null, activity)
        adapter = AlbumFolderAdapterNew(null, activity)
        rv!!.adapter = adapter
        adapter!!.setAnimationWithDefault(BaseQuickAdapter.AnimationType.AlphaIn)
        adapter!!.setOnItemClickListener { adapter, _, position ->
            if (AlbumClickUtil.isFastDoubleClick())
                return@setOnItemClickListener

            if (position == lastPosition) {
                if (activity != null) {
                    (activity as AlbumActivity?)!!.closeDrawer()
                }
                return@setOnItemClickListener
            }

            val bean = adapter.data[position] as AlbumFolder?
            bean?.let {
                it.isSelected = true
                adapter.notifyItemChanged(position)
                if (lastPosition == -1) {
                    lastPosition = 0
                }
                val lastBean = adapter.data[lastPosition] as AlbumFolder?
                lastBean?.let { itt ->
                    itt.isSelected = false
                    adapter.notifyItemChanged(lastPosition)
                }
                viewModel!!.setCurrentFolder(it)
                if (activity != null) {
                    (activity as AlbumActivity?)!!.closeDrawer()
                }
                lastPosition = position
            }
        }
        viewModel = ViewModelProvider(activity!!).get(AlbumViewModel::class.java)
//        compositeDisposable.add(
//                viewModel!!
//                        .getFolders()
//                        .compose(AlbumRxSchedulers.normalSchedulers())
//                        .subscribe { folders ->
//                            folders[0].isSelected = true
//                            adapter!!.setList(folders)
//                            viewModel!!.setCurrentFolder(folders[0])
//                        }
//        )
        compositeDisposable.add(
            viewModel!!
                .getFoldersNew()
                .compose(AlbumRxSchedulers.normalSchedulers())
                .subscribe { folders ->
                    adapter!!.setList(folders)
                    viewModel!!.setCurrentFolder(folders[0])
                }
        )
        return view
    }

    override fun onDestroyView() {
        if (compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
        super.onDestroyView()
    }

    fun setOnBackClickListener(clickListener: View.OnClickListener) {
        onClickListener = clickListener
    }

}