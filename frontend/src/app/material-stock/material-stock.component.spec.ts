import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MaterialStockComponent } from './material-stock.component';
import { MaterialStockService } from './material-stock.service';
import { of } from 'rxjs';

describe('MaterialStockComponent', () => {
  let component: MaterialStockComponent;
  let fixture: ComponentFixture<MaterialStockComponent>;
  let mockService: jasmine.SpyObj<MaterialStockService>;

  beforeEach(async () => {
    mockService = jasmine.createSpyObj('MaterialStockService', ['getStock']);
    mockService.getStock.and.returnValue(of({ data: [], count: 0 }));

    await TestBed.configureTestingModule({
      declarations: [MaterialStockComponent],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        NoopAnimationsModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatCheckboxModule,
        MatButtonModule,
        MatIconModule
      ],
      providers: [
        { provide: MaterialStockService, useValue: mockService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MaterialStockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with correct defaults', () => {
    expect(component.form.get('matnr')?.value).toBe('');
    expect(component.form.get('mtart')?.value).toBe('');
    expect(component.form.get('plant')?.value).toBe('');
    expect(component.form.get('language')?.value).toBe('E');
    expect(component.form.get('byPlant')?.value).toBe(false);
    expect(component.form.get('top')?.value).toBe(0);
  });

  it('should call service on submit', () => {
    component.onSubmit();
    expect(mockService.getStock).toHaveBeenCalled();
  });

  it('should have correct grid column definitions', () => {
    expect(component.columnDefs.length).toBe(6);
    expect(component.columnDefs.map(c => c.field)).toEqual([
      'matnr', 'maktx', 'mtart', 'meins', 'werks', 'labst'
    ]);
  });

  it('should set noDataMessage when response is empty', () => {
    mockService.getStock.and.returnValue(of({ data: [], count: 0 }));
    component.onSubmit();
    expect(component.noDataMessage).toBe('No data found for given selection');
  });

  it('should have report header containing "Material Stock Report"', () => {
    expect(component.reportHeader).toContain('Material Stock Report');
  });
});
