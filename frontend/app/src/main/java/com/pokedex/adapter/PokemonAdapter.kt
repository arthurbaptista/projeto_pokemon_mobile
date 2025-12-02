package com.pokedex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pokedex.data.model.Pokemon
import com.pokedex.databinding.ItemPokemonBinding

class PokemonAdapter(
    // Parâmetro novo: showType (padrão é true para não quebrar a lista geral)
    private val showType: Boolean = true,
    private val onItemClicked: (Pokemon) -> Unit
) : ListAdapter<Pokemon, PokemonAdapter.PokemonViewHolder>(PokemonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Passamos a configuração para o ViewHolder
        return PokemonViewHolder(binding, showType)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = getItem(position)
        holder.bind(pokemon)
        holder.itemView.setOnClickListener { onItemClicked(pokemon) }
    }

    class PokemonViewHolder(
        private val binding: ItemPokemonBinding,
        private val showType: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: Pokemon) {
            binding.textViewName.text = pokemon.nome

            // Lógica para mostrar ou esconder o tipo
            if (showType) {
                binding.textViewType.text = pokemon.tipo
                binding.textViewType.visibility = View.VISIBLE
            } else {
                binding.textViewType.visibility = View.GONE
            }
        }
    }
}

class PokemonDiffCallback : DiffUtil.ItemCallback<Pokemon>() {
    override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
        return oldItem == newItem
    }
}