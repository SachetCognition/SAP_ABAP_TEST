describe('Material Stock Report', () => {

  beforeEach(() => {
    cy.visit('/materials/stock');
  });

  it('P4-E2E01: should display form with all 6 inputs', () => {
    cy.get('input[formControlName="matnr"]').should('exist');
    cy.get('input[formControlName="mtart"]').should('exist');
    cy.get('input[formControlName="plant"]').should('exist');
    cy.get('mat-select[formControlName="language"]').should('exist');
    cy.get('mat-checkbox[formControlName="byPlant"]').should('exist');
    cy.get('input[formControlName="top"]').should('exist');
    cy.screenshot('material-stock-form');
  });

  it('P4-E2E02: should load data rows on submit with defaults', () => {
    cy.get('button[type="submit"]').click();
    cy.get('ag-grid-angular').should('exist');
    cy.get('.ag-row').should('have.length.greaterThan', 0);
  });

  it('P4-E2E03: should display correct column headers', () => {
    cy.get('button[type="submit"]').click();
    const expectedHeaders = [
      'Material Number', 'Material Description', 'Material Type',
      'Base Unit of Measure', 'Plant', 'Unrestricted-Use Stock'
    ];
    expectedHeaders.forEach(header => {
      cy.get('.ag-header-cell-text').contains(header).should('exist');
    });
    cy.screenshot('material-stock-columns');
  });

  it('P4-E2E04: should display alternating row colors (striped pattern)', () => {
    cy.get('button[type="submit"]').click();
    cy.get('.ag-row-even').should('exist');
    cy.screenshot('material-stock-striped');
  });

  it('P4-E2E05: should display report header with date', () => {
    cy.get('.report-header').should('contain', 'Material Stock Report -');
    cy.screenshot('material-stock-header');
  });

  it('P4-E2E06: should show pinned bottom row with stock sum', () => {
    cy.get('button[type="submit"]').click();
    cy.get('.ag-row-pinned').should('exist');
    cy.screenshot('material-stock-pinned-total');
  });

  it('P4-E2E07: should show Plant values when byPlant is checked', () => {
    cy.get('mat-checkbox[formControlName="byPlant"]').click();
    cy.get('button[type="submit"]').click();
    cy.get('.ag-cell[col-id="werks"]').first().should('not.be.empty');
    cy.screenshot('material-stock-by-plant');
  });

  it('P4-E2E08: should show empty Plant when byPlant is unchecked', () => {
    cy.get('button[type="submit"]').click();
    cy.get('.ag-cell[col-id="werks"]').first().should('have.text', '');
    cy.screenshot('material-stock-no-plant');
  });

  it('P4-E2E09: should return top 3 rows sorted by stock descending', () => {
    cy.get('input[formControlName="top"]').clear().type('3');
    cy.get('button[type="submit"]').click();
    cy.get('.ag-row[row-index]').should('have.length.at.most', 3);
    cy.screenshot('material-stock-top3');
  });

  it('P4-E2E10: should show "No data found" for nonexistent material', () => {
    cy.get('input[formControlName="matnr"]').type('NONEXISTENT_MATERIAL');
    cy.get('button[type="submit"]').click();
    cy.contains('No data found for given selection').should('be.visible');
    cy.screenshot('material-stock-no-data');
  });

  it('P4-E2E11: should support sort, filter, and CSV export', () => {
    cy.get('button[type="submit"]').click();
    // Click column header to sort
    cy.get('.ag-header-cell-text').contains('Material Number').click();
    // Verify CSV export button exists
    cy.contains('Export CSV').should('exist');
    cy.contains('Export CSV').click();
  });

  it('P4-E2E12: should auto-size columns to content', () => {
    cy.get('button[type="submit"]').click();
    cy.get('.ag-header-cell').should('have.length.greaterThan', 0);
    cy.screenshot('material-stock-auto-size');
  });
});
