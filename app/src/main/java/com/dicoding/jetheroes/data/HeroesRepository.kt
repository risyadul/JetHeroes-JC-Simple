package com.dicoding.jetheroes.data

import com.dicoding.jetheroes.model.Hero
import com.dicoding.jetheroes.model.HeroesData

class HeroesRepository {

    fun getHeroes() = HeroesData.heroes

    fun searchHeroes(query: String): List<Hero> {
        return HeroesData.heroes.filter {
            it.name.contains(query, true)
        }
    }
}