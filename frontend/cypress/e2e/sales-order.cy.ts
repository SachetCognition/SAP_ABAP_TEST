describe('Sales Order Details', () => {

  beforeEach(() => {
    cy.visit('/sales-orders');
  });

  it('P5-E2E01: should display form with material (required), sales order, language, max rows', () => {
    cy.get('input[formControlName="matnr"]').should('exist');
    cy.get('input[formControlName="vbeln"]').should('exist');
    cy.get('mat-select[formControlName="language"]').should('exist');
    cy.get('input[formControlName="maxRows"]').should('exist');
    cy.screenshot('sales-order-form');
  });

  it('P5-E2E02: should show validation error when submitting without material', () => {
    cy.get('button[type="submit"]').click();
    cy.get('mat-error').should('contain', 'Material is required');
    cy.screenshot('sales-order-validation-error');
  });

  it('P5-E2E03: should display grid with all 14 columns for valid material', () => {
    cy.get('input[formControlName="matnr"]').type('MAT001');
    cy.get('button[type="submit"]').click();
    cy.get('.ag-header-cell').should('have.length', 14);
    cy.screenshot('sales-order-grid-columns');
  });

  it('P5-E2E04: should show single result when specific vbeln entered', () => {
    cy.get('input[formControlName="matnr"]').type('MAT001');
    cy.get('input[formControlName="vbeln"]').type('0000000001');
    cy.get('button[type="submit"]').click();
    cy.get('.ag-row[row-index="0"]').should('exist');
    cy.screenshot('sales-order-single-result');
  });

  it('P5-E2E05: should return only 2 rows when maxRows=2', () => {
    cy.get('input[formControlName="matnr"]').type('MAT001');
    cy.get('input[formControlName="maxRows"]').clear().type('2');
    cy.get('button[type="submit"]').click();
    cy.get('.ag-row[row-index]').should('have.length.at.most', 2);
    cy.screenshot('sales-order-max-rows');
  });

  it('P5-E2E06: should show "No sales orders found" for material with no orders', () => {
    cy.get('input[formControlName="matnr"]').type('MAT004');
    cy.get('button[type="submit"]').click();
    cy.contains('No sales orders found').should('be.visible');
    cy.screenshot('sales-order-no-data');
  });
});
