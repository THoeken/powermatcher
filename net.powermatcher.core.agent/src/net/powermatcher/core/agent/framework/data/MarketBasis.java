package net.powermatcher.core.agent.framework.data;


import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * MarketBasis is an immutable type specifying the PowerMatcher market basis for bids and prices.
 * 
 * @author IBM
 * @version 0.9.0
 */
public class MarketBasis {
	/**
	 * Define the marketref mask (int) constant.
	 */
	private static final int MARKETREF_MASK = 0xFF;
	/**
	 * Define the root locale symbols (DecimalFormatSymbols) constant.
	 */
	public static DecimalFormatSymbols ROOT_SYMBOLS = DecimalFormatSymbols.getInstance(Locale.ROOT);
	/**
	 * Define the price format (DecimalFormat) constant.
	 */
	public static DecimalFormat PRICE_FORMAT = new DecimalFormat("0.##", ROOT_SYMBOLS);
	/**
	 * Define the demand format (DecimalFormat) constant.
	 */
	public static DecimalFormat DEMAND_FORMAT = new DecimalFormat("0.###E0", ROOT_SYMBOLS);

	/**
	 * Equals with the specified obj1 and obj2 parameters and return the boolean
	 * result.
	 * 
	 * @param obj1
	 *            The obj1 (<code>Object</code>) parameter.
	 * @param obj2
	 *            The obj2 (<code>Object</code>) parameter.
	 * @return Results of the equals (<code>boolean</code>) value.
	 * @see #equals(Object)
	 */
	private static boolean equals(final Object obj1, final Object obj2) {
		;
		return obj1 == obj2 || (obj1 != null && obj1.equals(obj2));
	}

	/**
	 * Define the commodity (String) field.
	 */
	private String commodity;
	/**
	 * Define the currency (String) field.
	 */
	private String currency;
	/**
	 * Define the price steps (int) field.
	 */
	private int priceSteps;
	/**
	 * Define the minimum price (double) field.
	 */
	private double minimumPrice;
	/**
	 * Define the maximum price (double) field.
	 */
	private double maximumPrice;
	/**
	 * Define the significance (int) field.
	 */
	private int significance;
	/**
	 * Define the market ref (int) field.
	 */
	private int marketRef;

	/**
	 * Define the zero price step (int) field.
	 */
	private int zeroPriceStep;

	/**
	 * Constructs an instance of this class from the specified commodity,
	 * currency, price steps, minimum price, maximum price, significance and
	 * market ref parameters.
	 * 
	 * @param commodity
	 *            The commodity (<code>String</code>) parameter.
	 * @param currency
	 *            The currency (<code>String</code>) parameter.
	 * @param priceSteps
	 *            The price steps (<code>int</code>) parameter.
	 * @param minimumPrice
	 *            The minimum price (<code>double</code>) parameter.
	 * @param maximumPrice
	 *            The maximum price (<code>double</code>) parameter.
	 * @param significance
	 *            The significance (<code>int</code>) parameter.
	 * @param marketRef
	 *            The market ref (<code>int</code>) parameter.
	 */
	public MarketBasis(final String commodity, final String currency, final int priceSteps, final double minimumPrice,
			final double maximumPrice, final int significance, final int marketRef) {
		if (priceSteps <= 0) {
			throw new InvalidParameterException("Price steps must be > 0.");
		}
		if (maximumPrice <= minimumPrice) {
			throw new InvalidParameterException("Maximum price must be > minimum price.");
		}
		if (significance < 0) {
			throw new InvalidParameterException("Significance must be >= 0.");
		}
		this.commodity = commodity;
		this.currency = currency;
		this.priceSteps = priceSteps;
		this.minimumPrice = minimumPrice;
		this.maximumPrice = maximumPrice;
		this.significance = significance;
		this.marketRef = marketRef & MARKETREF_MASK;
		this.zeroPriceStep = toPriceStep(0.0d);
	}

	/**
	 * Bound normalized price with the specified normalized price parameter and
	 * return the int result.
	 * 
	 * @param normalizedPrice
	 *            The normalized price (<code>int</code>) parameter.
	 * @return Results of the bound normalized price (<code>int</code>) value.
	 * @see #toNormalizedPrice(double)
	 * @see #toNormalizedPrice(int)
	 */
	public int boundNormalizedPrice(final int normalizedPrice) {
		int boundedNormalizedPrice = Math.min(normalizedPrice, this.priceSteps - this.zeroPriceStep - 1);
		boundedNormalizedPrice = Math.max(boundedNormalizedPrice, -this.zeroPriceStep);
		return boundedNormalizedPrice;
	}

	/**
	 * Bound price with the specified price parameter and return the double
	 * result.
	 * 
	 * @param price
	 *            The price (<code>double</code>) parameter.
	 * @return Results of the bound price (<code>double</code>) value.
	 * @see #boundNormalizedPrice(int)
	 * @see #getMaximumPrice()
	 * @see #getMinimumPrice()
	 * @see #roundPrice(double)
	 * @see #toNormalizedPrice(double)
	 * @see #toNormalizedPrice(int)
	 * @see #toPrice(int)
	 */
	public double boundPrice(final double price) {
		double boundedPrice = Math.min(price, this.maximumPrice);
		boundedPrice = Math.max(boundedPrice, this.minimumPrice);
		return boundedPrice;
	}

	/**
	 * Bound price step with the specified price step parameter and return the
	 * int result.
	 * 
	 * @param priceStep
	 *            The price step (<code>int</code>) parameter.
	 * @return Results of the bound price step (<code>int</code>) value.
	 * @see #toPriceStep(double)
	 * @see #toPriceStep(int)
	 */
	public int boundPriceStep(final int priceStep) {
		int boundedPriceStep = Math.min(priceStep, this.priceSteps - 1);
		boundedPriceStep = Math.max(boundedPriceStep, 0);
		return boundedPriceStep;
	}

	/**
	 * Equals with the specified obj parameter and return the boolean result.
	 * 
	 * @param obj
	 *            The obj (<code>Object</code>) parameter.
	 * @return Results of the equals (<code>boolean</code>) value.
	 */
	@Override
	public boolean equals(final Object obj) {
		MarketBasis other = (MarketBasis) ((obj instanceof MarketBasis) ? obj : null);
		return this == other
				|| (other != null && other.marketRef == this.marketRef && equals(other.commodity, this.commodity)
						&& equals(other.currency, this.currency) && other.priceSteps == this.priceSteps
						&& other.minimumPrice == this.minimumPrice && other.maximumPrice == this.maximumPrice && other.significance == this.significance);
	}

	/**
	 * Gets the commodity (String) value.
	 * 
	 * @return The commodity (<code>String</code>) value.
	 */
	public String getCommodity() {
		return this.commodity;
	}

	/**
	 * Gets the 3 character currency code value.
	 * 
	 * @return The currency (<code>String</code>) value.
	 */
	public String getCurrency() {
		return this.currency;
	}

	/**
	 * Gets the market ref (int) value.
	 * 
	 * @return The market ref (<code>int</code>) value.
	 */
	public int getMarketRef() {
		return this.marketRef;
	}

	/**
	 * Gets the maximum price (double) value.
	 * 
	 * @return The maximum price (<code>double</code>) value.
	 */
	public double getMaximumPrice() {
		return this.maximumPrice;
	}

	/**
	 * Gets the minimum price (double) value.
	 * 
	 * @return The minimum price (<code>double</code>) value.
	 */
	public double getMinimumPrice() {
		return this.minimumPrice;
	}

	/**
	 * Gets the price increment (double) value.
	 * 
	 * @return The price increment (<code>double</code>) value.
	 */
	public final double getPriceIncrement() {
		return (this.maximumPrice - this.minimumPrice) / (this.priceSteps - 1);
	}

	/**
	 * Gets the price steps (int) value.
	 * 
	 * @return The price steps (<code>int</code>) value.
	 */
	public int getPriceSteps() {
		return this.priceSteps;
	}

	/**
	 * Gets the significance (int) value.
	 * 
	 * @return The significance (<code>int</code>) value.
	 */
	public int getSignificance() {
		return this.significance;
	}

	/**
	 * Hash code and return the int result.
	 * 
	 * @return Results of the hash code (<code>int</code>) value.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + ((this.commodity == null) ? 0 : this.commodity.hashCode());
		result = prime * result + ((this.currency == null) ? 0 : this.currency.hashCode());
		result = prime * result + this.marketRef;
		long temp = Double.doubleToLongBits(this.maximumPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(this.minimumPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + this.priceSteps;
		result = prime * result + this.significance;
		return result;
	}

	/**
	 * Round price with the specified price parameter and return the double
	 * result.
	 * 
	 * @param price
	 *            The price (<code>double</code>) parameter.
	 * @return Results of the round price (<code>double</code>) value.
	 * @see #boundNormalizedPrice(int)
	 * @see #boundPrice(double)
	 * @see #getMaximumPrice()
	 * @see #getMinimumPrice()
	 * @see #toNormalizedPrice(double)
	 * @see #toNormalizedPrice(int)
	 * @see #toPrice(int)
	 */
	public double roundPrice(final double price) {
		if (this.significance >= 0 && this.significance <= 15) {
			BigDecimal bd = new BigDecimal(Double.toString(price));
			bd = bd.setScale(this.significance, BigDecimal.ROUND_HALF_UP);
			return bd.doubleValue();
		}
		return price;
	}

	/**
	 * To normalized price with the specified price parameter and return the int
	 * result.
	 * 
	 * @param price
	 *            The price (<code>double</code>) parameter.
	 * @return Results of the to normalized price (<code>int</code>) value.
	 * @see #boundNormalizedPrice(int)
	 * @see #toNormalizedPrice(int)
	 */
	public int toNormalizedPrice(final double price) {
		return toPriceStep(price) - this.zeroPriceStep;
	}

	/**
	 * To normalized price with the specified price step parameter and return
	 * the int result.
	 * 
	 * @param priceStep
	 *            The price step (<code>int</code>) parameter.
	 * @return Results of the to normalized price (<code>int</code>) value.
	 * @see #boundNormalizedPrice(int)
	 * @see #toNormalizedPrice(double)
	 */
	public int toNormalizedPrice(final int priceStep) {
		return priceStep - this.zeroPriceStep;
	}

	/**
	 * To price with the specified price step parameter and return the double
	 * result.
	 * 
	 * @param priceStep
	 *            The price step (<code>int</code>) parameter.
	 * @return Results of the to price (<code>double</code>) value.
	 * @see #boundNormalizedPrice(int)
	 * @see #boundPrice(double)
	 * @see #getMaximumPrice()
	 * @see #getMinimumPrice()
	 * @see #roundPrice(double)
	 * @see #toNormalizedPrice(double)
	 * @see #toNormalizedPrice(int)
	 */
	public double toPrice(final int priceStep) {
		return roundPrice(this.minimumPrice + priceStep * ((this.maximumPrice - this.minimumPrice) / (this.priceSteps - 1)));
	}

	/**
	 * To price step with the specified price parameter and return the int
	 * result.
	 * 
	 * @param price
	 *            The price (<code>double</code>) parameter.
	 * @return Results of the to price step (<code>int</code>) value.
	 * @see #boundPriceStep(int)
	 * @see #toPriceStep(int)
	 */
	public int toPriceStep(final double price) {
		double priceStep = ((price - this.minimumPrice) / (this.maximumPrice - this.minimumPrice)) * (this.priceSteps - 1);
		return Math.round((float) priceStep);
	}

	/**
	 * To price step with the specified normalized price parameter and return
	 * the int result.
	 * 
	 * @param normalizedPrice
	 *            The normalized price (<code>int</code>) parameter.
	 * @return Results of the to price step (<code>int</code>) value.
	 * @see #boundPriceStep(int)
	 * @see #toPriceStep(double)
	 */
	public int toPriceStep(final int normalizedPrice) {
		return this.zeroPriceStep + normalizedPrice;
	}

	/**
	 * Returns the string value.
	 * 
	 * @return The string (<code>String</code>) value.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("MarketBasis{commodity=").append(this.commodity);
		b.append(", currency=").append(this.currency);
		b.append(", minimumPrice=").append(PRICE_FORMAT.format(this.minimumPrice));
		b.append(", maximumPrice=").append(PRICE_FORMAT.format(this.maximumPrice));
		b.append(", priceSteps=").append(this.priceSteps);
		b.append(", significance=").append(this.significance);
		b.append(", marketRef=").append(this.marketRef);
		b.append('}');
		return b.toString();
	}

}
