package com.ceinsys.togethringtaskdemo.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.ceinsys.togethringtaskdemo.databinding.FragmentProfileBinding
import com.ceinsys.togethringtaskdemo.view.LoginScreen
import com.ceinsys.togethringtaskdemo.view.MainViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mainViewModel.readID.observe(viewLifecycleOwner,{
            binding.idTxt.text = it
        })

        mainViewModel.readName.observe(viewLifecycleOwner, {
            binding.nameTxt.text = it
        })

        mainViewModel.readEmail.observe(viewLifecycleOwner, {
            binding.emailTxt.text = it
        })

        mainViewModel.readPhoto.observe(viewLifecycleOwner, {
            binding.profileImage.load(it)
        })

        binding.signOutBtn.setOnClickListener {
            logoutApp()
        }
        return binding.root
    }

    private fun logoutApp() {
        mainViewModel.clearAllStoreData()
        startActivity(Intent(requireContext(), LoginScreen::class.java))
        activity?.finishAffinity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}