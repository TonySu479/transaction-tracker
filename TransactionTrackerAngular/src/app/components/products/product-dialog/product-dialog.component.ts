import {Component, OnInit} from '@angular/core';
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {FormGroup, FormControl, Validators, FormBuilder} from '@angular/forms'
import {ProductCategoryService} from "../../../service/product-category.service";
import {ProductCategory} from "../../../api/product-category";

@Component({
    selector: 'app-product-dialog',
    templateUrl: './product-dialog.component.html',
    styleUrls: ['./product-dialog.component.scss']
})
export class ProductDialogComponent implements OnInit {

    results: ProductCategory[];

    productForm: FormGroup;

    constructor(private ref: DynamicDialogRef,
                private config: DynamicDialogConfig,
                private formBuilder: FormBuilder,
                private productCategoryService: ProductCategoryService) {
    }

    get code() {
        return this.productForm.controls.code;
    }

    get name() {
        return this.productForm.controls.name;
    }

    get category() {
        return this.productForm.controls.category;
    }

    get price() {
        return this.productForm.controls.price;
    }

    get quantity() {
        return this.productForm.controls.quantity;
    }

    get unit() {
        return this.productForm.controls.unit;
    }

    get image() {
        return this.productForm.controls.image;
    }

    search(event) {
            this.productCategoryService.getProductCategories(event.query).subscribe(data => {
                this.results = data;
            });
    }

    ngOnInit(): void {
        this.productForm = this.formBuilder.group({
            code: new FormControl(this.config.data.type === "new" ? "" : this.config.data.product.code,
                [Validators.maxLength(10), Validators.required]),
            name: new FormControl(this.config.data.type === "new" ? "" : this.config.data.product.name,
                [Validators.maxLength(50), Validators.required]),
            category: new FormControl(this.config.data.type === "new" ? "" : this.config.data.product.category.name,
                [Validators.maxLength(200), Validators.required]),
            price: new FormControl(this.config.data.type === "new" ? "" : this.config.data.product.price,
                [Validators.min(0), Validators.required]),
            quantity: new FormControl(this.config.data.type === "new" ? "" : this.config.data.product.quantity,
                [Validators.required]),
            unit: new FormControl(this.config.data.type === "new" ? "" : this.config.data.product.unit,
                [Validators.maxLength(10), Validators.required]),
            image: new FormControl()
        });
    }

    submit() {
        if (!this.productForm.valid) {
            this.productForm.markAllAsTouched();
            return;
        }

        this.ref.close(this.productForm.value);
    }

}
