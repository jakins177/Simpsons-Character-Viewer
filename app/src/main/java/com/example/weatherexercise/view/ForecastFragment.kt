package com.example.weatherexercise.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherexercise.R
import com.example.weatherexercise.SimpsonModel.RelatedTopic
import com.example.weatherexercise.SimpsonModel.SimpsonsData
import com.example.weatherexercise.adapter.CharacterAdapter
import com.example.weatherexercise.adapter.ForecastAdapter
import com.example.weatherexercise.databinding.FragmentForecastBinding
import com.example.weatherexercise.model.ForecastData
import com.example.weatherexercise.util.ApiState
import com.example.weatherexercise.util.parseName
import com.example.weatherexercise.viewmodel.CharacterViewModel
import java.util.*


class ForecastFragment : Fragment() {
    private val TAG = "ForecastFragment"
    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!
    private lateinit var cList: List<RelatedTopic>
    private lateinit var cAdapter: CharacterAdapter

    //private val args: ForecastFragmentArgs by navArgs()
    private lateinit var wData: ForecastData
    private lateinit var simpData: SimpsonsData
    private val characterViewModel by activityViewModels<CharacterViewModel>()
    //private var characterSearchView: SearchView = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentForecastBinding.inflate(layoutInflater, container, false).also {
        _binding = it

//        cAdapter = CharacterAdapter(mList)
//        recyclerView.adapter = adapter
        characterViewModel.makeCharacterFetch("simpsons")
        //  wData = args.wData

        binding.contentSearchView.clearFocus()

        binding.contentSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText != "")
                    filterList(newText)
                return true
            }

        })

        setupObservers()

    }.root


    private fun initViews() = with(binding) {
        val isTablet = context?.resources?.getBoolean(R.bool.is_tablet)
        //setup recycle view
        rvList.hasFixedSize()
        rvList.layoutManager = LinearLayoutManager(context)
        rvList.itemAnimator = DefaultItemAnimator()

        //wData.list;
        cList = simpData.RelatedTopics
        cAdapter = CharacterAdapter(simpData.RelatedTopics) { item ->

            if (isTablet == true) {
                Log.i(TAG, "initViews: THIS IS A TABLET")
                initTabletViews(item)
            } else {
                Log.i(TAG, "initViews: THIS IS A PHONE")
                val directions =
                    ForecastFragmentDirections.actionForecastFragmentToForecastDetailFragment(item)
                view?.findNavController()?.navigate(directions)
            }

        }
        rvList.adapter = cAdapter


    }

    private fun initTabletViews(characterItem: RelatedTopic) = with(binding) {
        tabletDescTextView?.text = characterItem.Text.toString()
        tabletTitleTextView?.text = parseName(characterItem.Text.toString())

        val imageUrl = characterItem.Icon.URL
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            val completeImageUrl = "https://duckduckgo.com$imageUrl"
            binding.tabletImageView?.let {
                Glide.with(requireContext())
                    .load(completeImageUrl)
                    .placeholder(R.drawable.placeholder_image) // Replace with your placeholder image resource
                    .error(R.drawable.error_image) // Replace with your error image resource (optional)
                    .into(it)
            }

        } else {

            binding.tabletImageView?.setImageResource(R.drawable.place_holder)

        }


    }


    private fun setupObservers() = with(characterViewModel) {
        characterState.observe(viewLifecycleOwner) { state ->

            if (state is ApiState.Success) {

                val cData = state.data;

                Log.i(TAG, "setupObservers: cData is: $cData")

                simpData = cData
                initViews();

//                val directions =
//                    CityLookupFragmentDirections.actionCityLookupFragmentToForecastFragment(wData)
//                view?.findNavController()?.navigate(directions)

            }
        }


    }

    private fun filterList(query: String?) = with(binding) {
        if (query != null && cList != null) {
            val filteredList = mutableListOf<RelatedTopic>()
            for (i in cList) {
                if (i.Text.lowercase(Locale.ROOT).contains(query)) {

                    filteredList.add(i)

                }

            }
            if (filteredList.isEmpty()) {
                Toast.makeText(requireContext(), "No Data found", Toast.LENGTH_SHORT).show()
            } else {

                cAdapter.setFilteredList(filteredList)

                rvList.adapter = cAdapter
            }

        }

    }


}