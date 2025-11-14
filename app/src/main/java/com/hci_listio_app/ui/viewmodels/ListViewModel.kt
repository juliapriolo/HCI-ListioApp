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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListUiState(
    val listId: Long? = null,
    val listName: String = "",
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
    val isCreatingProduct: Boolean = false
)

class ListViewModel(
    private val listRepository: ListRepository = ListRepositoryProvider.instance,
    private val productsRepository: AllProductsRepository = AllProductsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    // Propiedades de compatibilidad con la implementación anterior
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

    // Cargar items de la lista desde la API
    fun loadList(listId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, listId = listId) }

            val result = listRepository.getList(listId)

            _uiState.update { current ->
                if (result.isSuccess) {
                    val listData = result.getOrNull()!!
                    val items = listData.items.map { item ->
                        ListItemData(
                            id = item.id.toString(),
                            name = item.productName,
                            isChecked = item.purchased
                        )
                    }
                    val participants = when {
                        listData.users.isNotEmpty() -> listData.users
                        listData.sharedWith.isNotEmpty() -> listData.sharedWith
                        else -> emptyList()
                    }
                    val ownerProfile = listData.owner ?: participants.firstOrNull()
                    val collaborators = ownerProfile?.let { ownerUser ->
                        participants.filterNot { it.id == ownerUser.id }
                    } ?: participants
                    current.copy(
                        listName = listData.name,
                        owner = ownerProfile,
                        sharedMembers = collaborators,
                        items = items,
                        completedCount = items.count { it.isChecked },
                        totalCount = items.size,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar la lista."
                    )
                }
            }

            // Cargar productos y categorías después de cargar la lista
            loadProductsAndCategories()
        }
    }

    // Cargar todos los productos disponibles del usuario
    fun loadProductsAndCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingProducts = true) }

            val token = com.hci_listio_app.data.AuthRepositoryProvider.instance.authToken.value ?: ""

            // Cargar productos
            val productsResult = productsRepository.getAllProducts(token)

            // Cargar categorías
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
        }
    }

    // Crear un nuevo producto y agregarlo inmediatamente a la lista
    fun createProductAndAddToList(productName: String, categoryId: Long) {
        val listId = _uiState.value.listId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingProduct = true, errorMessage = null) }

            val token = com.hci_listio_app.data.AuthRepositoryProvider.instance.authToken.value ?: ""

            // Crear el producto
            val createResult = productsRepository.createProduct(token, productName, categoryId)

            if (createResult.isSuccess) {
                val newProduct = createResult.getOrNull()!!

                // Agregar el producto a la lista
                val addResult = listRepository.addItem(
                    listId = listId,
                    productName = newProduct.name,
                    productId = newProduct.id,
                    categoryId = newProduct.categoryId
                )

                _uiState.update { current ->
                    if (addResult.isSuccess) {
                        val newItem = addResult.getOrNull()!!
                        val newListItem = ListItemData(
                            id = newItem.id.toString(),
                            name = newItem.productName,
                            isChecked = newItem.purchased
                        )
                        val updatedItems = current.items + newListItem
                        current.copy(
                            items = updatedItems,
                            totalCount = updatedItems.size,
                            completedCount = updatedItems.count { it.isChecked },
                            availableProducts = current.availableProducts + newProduct,
                            isCreatingProduct = false,
                            errorMessage = null
                        )
                    } else {
                        current.copy(
                            isCreatingProduct = false,
                            errorMessage = addResult.exceptionOrNull()?.message ?: "Error al agregar el producto a la lista."
                        )
                    }
                }
            } else {
                _uiState.update { current ->
                    current.copy(
                        isCreatingProduct = false,
                        errorMessage = createResult.exceptionOrNull()?.message ?: "Error al crear el producto."
                    )
                }
            }
        }
    }

    // Agregar un producto existente a la lista
    fun addExistingProductToList(product: Product) {
        val listId = _uiState.value.listId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = listRepository.addItem(
                listId = listId,
                productName = product.name,
                productId = product.id,
                categoryId = product.categoryId
            )

            _uiState.update { current ->
                if (result.isSuccess) {
                    val newItem = result.getOrNull()!!
                    val newListItem = ListItemData(
                        id = newItem.id.toString(),
                        name = newItem.productName,
                        isChecked = newItem.purchased
                    )
                    val updatedItems = current.items + newListItem
                    current.copy(
                        items = updatedItems,
                        totalCount = updatedItems.size,
                        completedCount = updatedItems.count { it.isChecked },
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al agregar el producto."
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = listRepository.shareListWithEmail(listId, trimmedEmail)

            if (result.isSuccess) {
                loadList(listId)
            } else {
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al compartir la lista."
                    )
                }
            }
        }
    }

    fun removeUserFromList(userId: Long) {
        val listId = _uiState.value.listId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = listRepository.removeUserFromList(listId, userId)

            if (result.isSuccess) {
                loadList(listId)
            } else {
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al quitar el usuario."
                    )
                }
            }
        }
    }

    // Actualizar el estado de check de un item (usando PATCH para purchased)
    fun toggleItemCheck(itemId: String, isChecked: Boolean) {
        val listId = _uiState.value.listId ?: return
        val itemIdLong = itemId.toLongOrNull() ?: return

        viewModelScope.launch {
            // Actualizar localmente primero (optimistic update)
            _uiState.update { current ->
                val updatedItems = current.items.map { item ->
                    if (item.id == itemId) item.copy(isChecked = isChecked) else item
                }
                current.copy(
                    items = updatedItems,
                    completedCount = updatedItems.count { it.isChecked }
                )
            }

            // Actualizar en la API usando togglePurchased
            val result = listRepository.togglePurchased(
                listId = listId,
                itemId = itemIdLong,
                purchased = isChecked
            )

            if (result.isFailure) {
                // Revertir el cambio si falla
                _uiState.update { current ->
                    val revertedItems = current.items.map { i ->
                        if (i.id == itemId) i.copy(isChecked = !isChecked) else i
                    }
                    current.copy(
                        items = revertedItems,
                        completedCount = revertedItems.count { it.isChecked },
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al actualizar el item."
                    )
                }
            }
        }
    }

    // Agregar un nuevo item (mantenido para compatibilidad)
    fun addItem(name: String) {
        val listId = _uiState.value.listId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = listRepository.addItem(
                listId = listId,
                productName = name
            )

            _uiState.update { current ->
                if (result.isSuccess) {
                    val newItem = result.getOrNull()!!
                    val newListItem = ListItemData(
                        id = newItem.id.toString(),
                        name = newItem.productName,
                        isChecked = newItem.purchased
                    )
                    val updatedItems = current.items + newListItem
                    current.copy(
                        items = updatedItems,
                        totalCount = updatedItems.size,
                        completedCount = updatedItems.count { it.isChecked },
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al agregar el item."
                    )
                }
            }
        }
    }

    // Editar un item existente
    fun editItem(itemId: String, newName: String, quantity: String, unit: String, brand: String, store: String) {
        val listId = _uiState.value.listId ?: return
        val itemIdLong = itemId.toLongOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Convertir quantity de String a Int si es posible
            val quantityInt = quantity.toIntOrNull()

            val result = listRepository.updateItem(
                listId = listId,
                itemId = itemIdLong,
                productName = newName,
                quantity = quantityInt
            )

            _uiState.update { current ->
                if (result.isSuccess) {
                    val updatedItem = result.getOrNull()!!
                    val updatedItems = current.items.map { item ->
                        if (item.id == itemId) {
                            item.copy(name = updatedItem.productName)
                        } else {
                            item
                        }
                    }
                    current.copy(
                        items = updatedItems,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al editar el item."
                    )
                }
            }
        }
    }

    // Eliminar un item
    fun deleteItem(itemId: String) {
        val listId = _uiState.value.listId ?: return
        val itemIdLong = itemId.toLongOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = listRepository.deleteItem(listId, itemIdLong)

            _uiState.update { current ->
                if (result.isSuccess) {
                    val updatedItems = current.items.filter { it.id != itemId }
                    current.copy(
                        items = updatedItems,
                        totalCount = updatedItems.size,
                        completedCount = updatedItems.count { it.isChecked },
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al eliminar el item."
                    )
                }
            }
        }
    }

    // Limpiar mensaje de error
    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}