package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.ListRepository
import com.hci_listio_app.data.ListRepositoryProvider
import com.hci_listio_app.data.remote.dto.UserProfileResponse
import com.hci_listio_app.ui.Components.ListItemData
import com.hci_listio_app.data.model.Product
import com.hci_listio_app.data.repository.AllProductsRepository
import com.hci_listio_app.data.remote.NetworkModule
import com.hci_listio_app.data.remote.dto.CategoryResponse
import com.hci_listio_app.ui.Components.ListItemsFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListUiState(
    val listId: Long? = null,
    val listName: String = "",
    val description: String? = null,
    val items: List<ListItemData> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val owner: UserProfileResponse? = null,
    val sharedMembers: List<UserProfileResponse> = emptyList(),
    val availableProducts: List<Product> = emptyList(),
    val isLoadingProducts: Boolean = false,
    val categories: List<CategoryResponse> = emptyList(),
    val isCreatingProduct: Boolean = false,
    val filter: ListItemsFilter = ListItemsFilter()
)

class ListViewModel(
    private val listRepository: ListRepository = ListRepositoryProvider.instance,
    private val productsRepository: AllProductsRepository = AllProductsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private val _items = MutableStateFlow<List<ListItemData>>(emptyList())
    val items: StateFlow<List<ListItemData>> = _items

    private val _completedCount = MutableStateFlow(0)
    val completedCount: StateFlow<Int> = _completedCount

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount

    init {
        viewModelScope.launch {
            _uiState.collect { state ->
                _items.value = state.items
                _completedCount.value = state.completedCount
                _totalCount.value = state.totalCount
            }
        }
    }

    fun loadList(listId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, listId = listId)
            }

            val listResult = listRepository.getList(listId)

            if (listResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = listResult.exceptionOrNull()?.message
                            ?: "Error al cargar la lista."
                    )
                }
                return@launch
            }

            val listData = listResult.getOrNull()!!

            _uiState.update { current ->
                val participants = when {
                    listData.users.isNotEmpty() -> listData.users
                    listData.sharedWith.isNotEmpty() -> listData.sharedWith
                    else -> emptyList()
                }

                val owner = listData.owner ?: participants.firstOrNull()
                val collaborators = owner?.let { o ->
                    participants.filterNot { it.id == o.id }
                } ?: participants

                current.copy(
                    listName = listData.name,
                    description = listData.description,
                    owner = owner,
                    sharedMembers = collaborators,
                    isLoading = false
                )
            }
            loadItems(listId)
            loadProductsAndCategories()
        }
    }

    fun loadProductsAndCategories() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoadingProducts = true) }

                val token = com.hci_listio_app.data.AuthRepositoryProvider.instance.authToken.value ?: ""

                if (token.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoadingProducts = false,
                            errorMessage = "No hay token de autenticación"
                        )
                    }
                    return@launch
                }

                val productsResult = productsRepository.getAllProducts(token)

                val categoriesResult = try {
                    val response = NetworkModule.categoryApiService.getCategories("Bearer $token")
                    Result.success(response.data)
                } catch (e: Exception) {
                    Result.failure(e)
                }

                _uiState.update { current ->
                    current.copy(
                        availableProducts = productsResult.getOrNull() ?: emptyList(),
                        categories = categoriesResult.getOrNull() ?: emptyList(),
                        isLoadingProducts = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingProducts = false,
                        errorMessage = "Error al cargar productos: ${e.message}"
                    )
                }
            }
        }
    }

    fun createProductAndAddToList(productName: String, categoryId: Long) {
        val listId = _uiState.value.listId

        if (listId == null) {
            _uiState.update { it.copy(errorMessage = "ID de lista no disponible") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isCreatingProduct = true, errorMessage = null) }

                val token = com.hci_listio_app.data.AuthRepositoryProvider.instance.authToken.value

                if (token.isNullOrEmpty()) {
                    _uiState.update {
                        it.copy(
                            isCreatingProduct = false,
                            errorMessage = "No hay token de autenticación"
                        )
                    }
                    return@launch
                }

                val createResult = productsRepository.createProduct(token, productName, categoryId)

                if (createResult.isFailure) {
                    _uiState.update {
                        it.copy(
                            isCreatingProduct = false,
                            errorMessage = createResult.exceptionOrNull()?.message
                                ?: "Error al crear el producto"
                        )
                    }
                    return@launch
                }

                val newProduct = createResult.getOrNull()

                if (newProduct == null) {
                    _uiState.update {
                        it.copy(
                            isCreatingProduct = false,
                            errorMessage = "No se pudo crear el producto"
                        )
                    }
                    return@launch
                }

                val addResult = listRepository.addItem(
                    listId = listId,
                    productId = newProduct.id,
                    quantity = 1
                )

                if (addResult.isFailure) {
                    _uiState.update {
                        it.copy(
                            isCreatingProduct = false,
                            errorMessage = addResult.exceptionOrNull()?.message
                                ?: "Error al agregar el producto a la lista"
                        )
                    }
                    return@launch
                }

                _uiState.update { it.copy(isCreatingProduct = false) }
                loadList(listId)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreatingProduct = false,
                        errorMessage = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    fun addExistingProductToList(product: Product) {
        val listId = _uiState.value.listId

        if (listId == null) {
            _uiState.update { it.copy(errorMessage = "ID de lista no disponible") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val result = listRepository.addItem(
                    listId = listId,
                    productId = product.id,
                    quantity = 1
                )

                if (result.isFailure) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exceptionOrNull()?.message
                                ?: "Error al agregar el producto"
                        )
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = false) }
                loadList(listId)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    fun shareListWithUser(email: String) {
        val listId = _uiState.value.listId ?: return
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty()) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val result = listRepository.shareListWithEmail(listId, trimmedEmail)

                if (result.isSuccess) {
                    loadList(listId)
                } else {
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = result.exceptionOrNull()?.message
                                ?: "Error al compartir la lista"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    fun removeUserFromList(userId: Long) {
        val listId = _uiState.value.listId ?: return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val result = listRepository.removeUserFromList(listId, userId)

                if (result.isSuccess) {
                    loadList(listId)
                } else {
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = result.exceptionOrNull()?.message
                                ?: "Error al quitar el usuario"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    fun toggleItemCheck(itemId: String, isChecked: Boolean) {
        val listId = _uiState.value.listId ?: return
        val itemIdLong = itemId.toLongOrNull()

        if (itemIdLong == null) {
            _uiState.update { it.copy(errorMessage = "ID de item inválido") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { current ->
                    val updatedItems = current.items.map { item ->
                        if (item.id == itemId) item.copy(isChecked = isChecked) else item
                    }
                    current.copy(
                        items = updatedItems,
                        completedCount = updatedItems.count { it.isChecked }
                    )
                }

                val result = listRepository.togglePurchased(
                    listId = listId,
                    itemId = itemIdLong,
                    purchased = isChecked
                )

                if (result.isFailure) {
                    // Revertir cambio
                    _uiState.update { current ->
                        val revertedItems = current.items.map { i ->
                            if (i.id == itemId) i.copy(isChecked = !isChecked) else i
                        }
                        current.copy(
                            items = revertedItems,
                            completedCount = revertedItems.count { it.isChecked },
                            errorMessage = result.exceptionOrNull()?.message
                                ?: "Error al actualizar el item"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Error inesperado: ${e.message}")
                }
            }
        }
    }

    fun editItem(itemId: String, quantity: String, unit: String) {
        val listId = _uiState.value.listId ?: return
        val itemIdLong = itemId.toLongOrNull()

        if (itemIdLong == null) {
            _uiState.update { it.copy(errorMessage = "ID de item inválido") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val quantityInt = quantity.toIntOrNull() ?: 1

                val result = listRepository.updateItem(
                    listId = listId,
                    itemId = itemIdLong,
                    quantity = quantityInt,
                    unit = unit
                )

                if (result.isFailure) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exceptionOrNull()?.message
                                ?: "Error al editar el item"
                        )
                    }
                    return@launch
                }

                val updatedItem = result.getOrNull()

                if (updatedItem == null || updatedItem.product == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al obtener el item actualizado"
                        )
                    }
                    return@launch
                }

                val newItems = _uiState.value.items.map { item ->
                    if (item.id == itemId) {
                        item.copy(
                            quantity = updatedItem.quantity,
                            name = updatedItem.product.name,
                            isChecked = updatedItem.purchased,
                            unit = updatedItem.unit
                        )
                    } else item
                }

                _uiState.update {
                    it.copy(
                        items = newItems,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteItem(itemId: String) {
        val listId = _uiState.value.listId ?: return
        val itemIdLong = itemId.toLongOrNull()

        if (itemIdLong == null) {
            _uiState.update { it.copy(errorMessage = "ID de item inválido") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val result = listRepository.deleteItem(listId, itemIdLong)

                if (result.isFailure) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exceptionOrNull()?.message
                                ?: "Error al eliminar el item"
                        )
                    }
                    return@launch
                }

                val updatedItems = _uiState.value.items.filter { it.id != itemId }

                _uiState.update {
                    it.copy(
                        items = updatedItems,
                        totalCount = updatedItems.size,
                        completedCount = updatedItems.count { it.isChecked },
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun applyFilter(filter: ListItemsFilter) {
        _uiState.update { it.copy(filter = filter) }

        val listId = uiState.value.listId ?: return

        viewModelScope.launch {
            loadItems(listId)
        }
    }

    private suspend fun loadItems(listId: Long) {
        val filter = uiState.value.filter

        val result = listRepository.getItems(
            listId = listId,
            purchased = filter.purchased,
            categoryId = filter.categoryId,
            search = filter.search,
            sortBy = filter.sortBy,
            order = filter.order
        )

        if (result.isSuccess) {
            val items = result.getOrNull()!!

            val mapped = items.map { item ->
                ListItemData(
                    id = item.id.toString(),
                    name = item.product!!.name,
                    isChecked = item.purchased,
                    productId = item.product.id,
                    quantity = item.quantity,
                    unit = item.unit
                )
            }

            _uiState.update {
                it.copy(
                    items = mapped,
                    completedCount = mapped.count { i -> i.isChecked },
                    totalCount = mapped.size
                )
            }
        } else {
            _uiState.update {
                it.copy(errorMessage = "Error al cargar items")
            }
        }
    }
}