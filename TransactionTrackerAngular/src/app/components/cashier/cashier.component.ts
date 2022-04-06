import {Component, OnInit} from '@angular/core';
import {ConfirmationService, MessageService} from "primeng/api";
import {DialogService} from "primeng/dynamicdialog";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {Product} from "../../api/product";
import {TransactionService} from "../../service/transactionservice";
import {ProductService} from "../../service/productservice";
import {TransactionDetail} from "../../api/transaction-detail";
import {TransactionDetailService} from "../../service/transaction-detail.service";
import {Transaction} from "../../api/transaction";
import {TransactionType} from "../../api/transaction-type.enum";

@Component({
    selector: 'app-cashier',
    templateUrl: './cashier.component.html',
    styleUrls: ['./cashier.component.scss'],
    providers: [MessageService, ConfirmationService, DialogService]
})
export class CashierComponent implements OnInit {

    cashierForm: FormGroup;
    productResults: Product[];
    transactionDetails: TransactionDetail[] = [];
    transaction: Transaction;

    constructor(private confirmationService: ConfirmationService,
                private formBuilder: FormBuilder,
                private transactionService: TransactionService,
                private transactionDetailsService: TransactionDetailService,
                private productService: ProductService,
                private messageService: MessageService) {
    }

    get product() {
        return this.cashierForm.controls.product;
    }

    get quantity() {
        return this.cashierForm.controls.quantity;
    }

    addProduct() {
        if (!this.cashierForm.valid) {
            this.cashierForm.markAllAsTouched();
            return;
        }
        const {quantity, product} = this.cashierForm.value;
        this.transactionDetails.push({
            quantity,
            product
        })
    }

    deleteTransactionDetail(index) {
        this.transactionDetails.splice(index, 1);
    }

    searchProducts(event) {
        this.productService.getProducts(event.query).subscribe(data => {
            this.productResults = data;
        });
    }

    ngOnInit(): void {
        this.cashierForm = this.formBuilder.group({
            product: new FormControl("", [Validators.required]),
            quantity: new FormControl("", [Validators.min(0), Validators.required])
        })
    }

}
