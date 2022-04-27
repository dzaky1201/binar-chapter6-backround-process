package com.dzakyhdr.moviedb.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.dzakyhdr.moviedb.MyApplication
import com.dzakyhdr.moviedb.R
import com.dzakyhdr.moviedb.databinding.FragmentDetailBinding
import com.dzakyhdr.moviedb.utils.urlImage
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()

    private val viewModel by viewModels<DetailViewModel>{
        DetailViewModelFactory((activity?.application as MyApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDetail(args.movieId)

        binding.ivBack.setOnClickListener {
            it.findNavController().popBackStack()
        }

        viewModel.loading.observe(viewLifecycleOwner){
            if (it){
                binding.loadingContainer.visibility = View.VISIBLE
            } else{
                binding.loadingContainer.visibility = View.GONE
            }
        }


        viewModel.detail.observe(viewLifecycleOwner) { it ->

            Glide.with(binding.ivBackdrop)
                .load(urlImage + it?.backdropPath)
                .error(R.drawable.ic_broken)
                .into(binding.ivBackdrop)

            Glide.with(binding.ivPoster)
                .load(urlImage + it?.posterPath)
                .error(R.drawable.ic_broken)
                .into(binding.ivPoster)

            binding.apply {
                tvTitle.text = it?.title
                tvVoteCount.text = it?.voteCount.toString()
                tvOverview.text = it?.overview
                it?.voteAverage.let {
                    if (it != null) {
                        rbRating.rating = (it / 2).toFloat()
                    }
                }

                if (it?.releaseDate != null && it.releaseDate.isNotBlank()){
                    tvReleaseDate.text = it.releaseDate
                }else{
                    tvReleaseDate.visibility = View.GONE
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}