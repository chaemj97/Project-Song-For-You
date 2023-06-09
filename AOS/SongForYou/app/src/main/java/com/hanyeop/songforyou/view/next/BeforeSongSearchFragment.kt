package com.hanyeop.songforyou.view.next

import android.content.Intent
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hanyeop.songforyou.R
import com.hanyeop.songforyou.base.BaseFragment
import com.hanyeop.songforyou.databinding.FragmentBeforeSongSearchBinding
import com.hanyeop.songforyou.model.response.SongResponse
import com.hanyeop.songforyou.utils.SONG
import com.hanyeop.songforyou.view.detail.SongDetailActivity
import com.hanyeop.songforyou.view.search.BeforeSongSearchAdapter
import com.hanyeop.songforyou.view.search.SongSearchAdapter
import com.hanyeop.songforyou.view.search.SongSearchListener
import com.hanyeop.songforyou.view.search.SongSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BeforeSongSearchFragment : BaseFragment<FragmentBeforeSongSearchBinding>(R.layout.fragment_before_song_search) {

    private lateinit var songSearchAdapter: BeforeSongSearchAdapter
    private val songSearchViewModel by viewModels<SongSearchViewModel>()
    private val nextSongRecommendViewModel by activityViewModels<NextSongRecommendViewModel>()

    override fun init() {
        songSearchAdapter = BeforeSongSearchAdapter(songSearchListener)

        binding.apply {
            recyclerSearch.adapter = songSearchAdapter
        }

        initViewModelCallBack()
        initSearchView()
        initObserver()
    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                songSearchViewModel.songSearch(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun initViewModelCallBack(){
        lifecycleScope.launch {
            songSearchViewModel.resultList.collectLatest {
                songSearchAdapter.submitList(it)
                binding.tvCount.text = it.size.toString()
            }
        }
    }
    private fun initObserver(){
        nextSongRecommendViewModel.successEvent.observe(viewLifecycleOwner) {
            if(it){
                val action = BeforeSongSearchFragmentDirections.actionBeforeSongSearchFragmentToNextSongRecommendFragment()
                findNavController().navigate(action)
            }
        }
    }
    private val songSearchListener = object : SongSearchListener {
        override fun onItemClick(song: SongResponse) {
            Log.d("test5", "onItemClick: $song")
            // 아이템 클릭 시 해당 아이템의 정보가 다음 프레그먼트로 넘어감 // 뷰모델에 저장
            nextSongRecommendViewModel.getNextSongRecommend(song.SongSeq, song.SongTitle)
        }

        override fun onRecordClick(song: SongResponse) {
            TODO("Not yet implemented")
        }
    }
}