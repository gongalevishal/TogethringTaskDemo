package com.ceinsys.togethringtaskdemo.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceinsys.togethringtaskdemo.R
import com.ceinsys.togethringtaskdemo.adapter.UserAdapter
import com.ceinsys.togethringtaskdemo.databinding.FragmentListBinding
import com.ceinsys.togethringtaskdemo.model.User
import com.ceinsys.togethringtaskdemo.viewmodel.UserViewModel

class ListFragment : Fragment(),SearchView.OnQueryTextListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private val userAdapter: UserAdapter by lazy { UserAdapter(arrayListOf()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(layoutInflater, container, false)

        // Set Menu
        setHasOptionsMenu(true)

        viewModel.refresh()

        binding.recyclerViewUser.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        binding.refreshLayoutUser.setOnRefreshListener {
            binding.recyclerViewUser.visibility = View.GONE
            binding.listError.visibility = View.GONE
            binding.progressLoadingUser.visibility = View.VISIBLE
            viewModel.refreshBypassCache()
            binding.refreshLayoutUser.isRefreshing = false
        }

        observeViewModel()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }


    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                binding.recyclerViewUser.visibility = View.VISIBLE
                userAdapter.updateUserList(user)
            }
        })

        viewModel.userLoadError.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.listError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->

            isLoading?.let {
                binding.progressLoadingUser.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    binding.listError.visibility = View.GONE
                    binding.recyclerViewUser.visibility = View.GONE
                }
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"

        viewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner,{

        })

        viewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner, { list ->
            list?.let {
               val finalList =  it.toCollection(ArrayList()) //convert L to AL
                userAdapter.setData(finalList)
            }
        })
    }
}