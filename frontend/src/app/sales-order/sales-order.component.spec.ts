import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { SalesOrderComponent } from './sales-order.component';
import { SalesOrderService } from './sales-order.service';
import { of } from 'rxjs';

describe('SalesOrderComponent', () => {
  let component: SalesOrderComponent;
  let fixture: ComponentFixture<SalesOrderComponent>;
  let mockService: jasmine.SpyObj<SalesOrderService>;

  beforeEach(async () => {
    mockService = jasmine.createSpyObj('SalesOrderService', ['getOrders']);
    mockService.getOrders.and.returnValue(of({ data: [], count: 0, messages: [] }));

    await TestBed.configureTestingModule({
      declarations: [SalesOrderComponent],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        NoopAnimationsModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatButtonModule,
        MatIconModule
      ],
      providers: [
        { provide: SalesOrderService, useValue: mockService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SalesOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should require matnr', () => {
    const matnrControl = component.form.get('matnr');
    expect(matnrControl?.hasError('required')).toBeTrue();
  });

  it('should not call service when form is invalid', () => {
    component.onSubmit();
    expect(mockService.getOrders).not.toHaveBeenCalled();
  });

  it('should call service with correct parameters', () => {
    component.form.patchValue({
      matnr: 'MAT001',
      vbeln: '0000000001',
      language: 'E',
      maxRows: 10
    });
    component.onSubmit();
    expect(mockService.getOrders).toHaveBeenCalledWith({
      matnr: 'MAT001',
      vbeln: '0000000001',
      language: 'E',
      maxRows: 10
    });
  });

  it('should have 14 column definitions', () => {
    expect(component.columnDefs.length).toBe(14);
  });

  it('should initialize form with correct defaults', () => {
    expect(component.form.get('language')?.value).toBe('E');
    expect(component.form.get('maxRows')?.value).toBe(0);
    expect(component.form.get('vbeln')?.value).toBe('');
  });
});
