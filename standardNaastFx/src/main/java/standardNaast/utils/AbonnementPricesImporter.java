package standardNaast.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import standardNaast.model.SeasonModel;
import standardNaast.service.AbonnementService;
import standardNaast.types.CompetitionType;
import standardNaast.types.PersonType;

public class AbonnementPricesImporter {

	private final AbonnementService abonnementService = new AbonnementService();

	public void importAbonnementsPrices(final String pathFileToImport, final SeasonModel season,
			final CompetitionType competitionType) {
		try {
			final FileInputStream file = new FileInputStream(new File(pathFileToImport));

			// Create Workbook instance holding reference to .xlsx file
			final XSSFWorkbook workbook = new XSSFWorkbook(file);

			final FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

			// Get first/desired sheet from the workbook
			final XSSFSheet sheet = workbook.getSheetAt(0);

			final List<BlocAbonnementPrices> prices = new ArrayList<>();

			// Iterate through each rows one by one
			final Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				final Row row = rowIterator.next();

				// For each row, iterate through all the columns
				final Iterator<Cell> cellIterator = row.cellIterator();

				final List<Cell> cellList = new ArrayList<>();
				while (cellIterator.hasNext()) {
					cellList.add(cellIterator.next());
				}

				final Cell blocCell = cellList.get(0);
				final Cell fullPrice = cellList.get(1);
				final Cell pensionedCell = cellList.get(2);
				final Cell between12and18Cell = cellList.get(3);
				final Cell lessThan12Cell = cellList.get(4);

				final String[] blocValue = blocCell.getStringCellValue().split("-");
				final double fullPriceValue = this.getCellValue(fullPrice, evaluator);
				final double pensionedPriceValue = this.getCellValue(pensionedCell, evaluator);
				final double between12and18PriceValue = this.getCellValue(between12and18Cell, evaluator);
				final double lessThan12PriceValue = this.getCellValue(lessThan12Cell, evaluator);

				for (final String bloc : blocValue) {
					BlocAbonnementPrices price = new BlocAbonnementPrices(bloc.trim(), fullPriceValue, PersonType.ADULT);
					prices.add(price);
					price = new BlocAbonnementPrices(bloc.trim(), pensionedPriceValue, PersonType.PENSIONED);
					prices.add(price);
					price = new BlocAbonnementPrices(bloc.trim(), between12and18PriceValue, PersonType.TWELVE_EIGHTEEN);
					prices.add(price);
					price = new BlocAbonnementPrices(bloc.trim(), lessThan12PriceValue, PersonType.LESS_THAN_TWELVE);
					prices.add(price);
				}

				this.abonnementService.addAbonnementPrices(prices, competitionType, season);

			}
			file.close();
			workbook.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private double getCellValue(final Cell cell, final FormulaEvaluator evaluator) {
		double value;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			value = cell.getNumericCellValue();
			break;
		case Cell.CELL_TYPE_STRING:
			value = Double.valueOf(cell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			switch (evaluator.evaluateInCell(cell).getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				value = cell.getNumericCellValue();
				break;
			case Cell.CELL_TYPE_STRING:
				value = Double.valueOf(cell.getStringCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				// Not again
				break;
			}
		case Cell.CELL_TYPE_BLANK:
			throw new UnsupportedOperationException("Cell of type [CELL_TYPE_BLANK] not supported");
		case Cell.CELL_TYPE_BOOLEAN:
			throw new UnsupportedOperationException("Cell value [" + cell.getBooleanCellValue()
					+ "] of type [CELL_TYPE_BOOLEAN] not supported");
		default:
			throw new UnsupportedOperationException("Cell of type [" + cell.getCellType() + "] not supported");
		}
		return value;
	}

	public class BlocAbonnementPrices {

		final String bloc;

		final double value;

		final PersonType personType;

		public BlocAbonnementPrices(final String bloc, final double value, final PersonType personType) {
			this.bloc = bloc;
			this.personType = personType;
			this.value = value;
		}

		public String getBloc() {
			return this.bloc;
		}

		public double getValue() {
			return this.value;
		}

		public PersonType getPersonType() {
			return this.personType;
		}

	}

	public static void main(final String args[]) {
		final SeasonModel season = new SeasonModel();
		season.setId("2015-2016");
		new AbonnementPricesImporter().importAbonnementsPrices("C:\\tmp\\abonnements.xlsx",
				season,
				CompetitionType.CHAMPIONSHIP);
	}

}
