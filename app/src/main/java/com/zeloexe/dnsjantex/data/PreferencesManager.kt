package com.zeloexe.dnsjantex.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zeloexe.dnsjantex.model.DnsServer

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("dns_jantex_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getSelectedDnsId(): String {
        return prefs.getString(KEY_SELECTED_DNS, "shecan") ?: "shecan"
    }

    fun setSelectedDnsId(id: String) {
        prefs.edit().putString(KEY_SELECTED_DNS, id).apply()
    }

    fun getCustomDnsList(): List<DnsServer> {
        val json = prefs.getString(KEY_CUSTOM_DNS, null) ?: return emptyList()
        val type = object : TypeToken<List<DnsServer>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveCustomDnsList(list: List<DnsServer>) {
        val json = gson.toJson(list)
        prefs.edit().putString(KEY_CUSTOM_DNS, json).apply()
    }

    fun addCustomDns(dns: DnsServer) {
        val current = getCustomDnsList().toMutableList()
        current.removeAll { it.id == dns.id }
        current.add(0, dns)
        saveCustomDnsList(current)
    }

    fun removeCustomDns(id: String) {
        val current = getCustomDnsList().toMutableList()
        current.removeAll { it.id == id }
        saveCustomDnsList(current)
    }

    fun getFavoriteIds(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, setOf("shecan", "electro", "cloudflare", "google")) ?: emptySet()
    }

    fun toggleFavorite(id: String) {
        val favs = getFavoriteIds().toMutableSet()
        if (favs.contains(id)) {
            favs.remove(id)
        } else {
            favs.add(id)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply()
    }

    companion object {
        private const val KEY_SELECTED_DNS = "selected_dns_id"
        private const val KEY_CUSTOM_DNS = "custom_dns_list"
        private const val KEY_FAVORITES = "favorite_dns_ids"
    }
}
