package com.android.ecommerceadmin.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.data.Product
import com.android.ecommerceadmin.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    val firestore: FirebaseFirestore
) : ViewModel() {


    private val _category = MutableLiveData<String>()
    val category = _category
    fun setCategory(category: String) {
        _category.value = category
    }

    private val _saveProduct = MutableStateFlow<Resource<Product>>(Resource.Undefined())
    val saveProduct = _saveProduct.asSharedFlow()

    private val _editProduct = MutableStateFlow<Resource<Product>>(Resource.Undefined())
    val editProduct = _editProduct.asSharedFlow()

    fun saveProduct(productName: String, category: String, price : Float,
                    offer:Float? = null, description: String? = null,
                    color : List<Int>?= null, sizes: List<String>? = null,
                    images: List<String>, stock : Int){
        //create product Object
        val product = Product(
            UUID.randomUUID().toString(),
            productName,
            category,
            price,
            offer,
            description,
            color,
            sizes,
            images,
            stock
        )

        //save product to FireStore
        viewModelScope.launch {
            _saveProduct.emit(Resource.Loading())
        }
        firestore.collection("products").add(product).addOnSuccessListener {
            viewModelScope.launch {
                _saveProduct.emit(Resource.Success(product))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _saveProduct.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun editProduct(
        productId: String,
        productName: String,
        category: String,
        price: Float,
        offer: Float? = null,
        description: String? = null,
        color: List<Int>? = null,
        sizes: List<String>? = null,
        images: List<String>,
        stock: Int
    ) {
        viewModelScope.launch {
            _editProduct.emit(Resource.Loading())
        }

        // Query Firestore to get the product by its 'productId' (or 'firebaseId')
        firestore.collection("products")
            .whereEqualTo("id", productId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // If product found, get the first document
                    val productDocument = querySnapshot.documents.first()
                    val product = productDocument.toObject(Product::class.java)

                    // Update the product with new values
                    product?.apply {
                        // Update fields if not null, otherwise keep old values
                        this.productName = productName
                        this.category = category
                        this.price = price
                        this.offer = offer
                        this.productDescription = description
                        this.color = color
                        this.sizes = sizes
                        this.images = images
                        this.stock = stock
                    }

                    // Save updated product back to Firestore using SetOptions.merge()
                    productDocument.reference.set(product!!, SetOptions.merge())
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                _editProduct.emit(Resource.Success(product))
                            }
                        }
                        .addOnFailureListener { e ->
                            viewModelScope.launch {
                                _editProduct.emit(Resource.Error(e.message.toString()))
                            }
                        }
                } else {
                    viewModelScope.launch {
                        _editProduct.emit(Resource.Error("Product not found"))
                    }
                }
            }
            .addOnFailureListener { e ->
                viewModelScope.launch {
                    _editProduct.emit(Resource.Error(e.message.toString()))
                }
            }
    }

}