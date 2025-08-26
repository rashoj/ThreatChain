import React from "react";

const IndicatorList = ({ indicators = [] }) => {
    if (indicators.length == 0) return null;

    return (
        <div className="mt-4">
            <h4 className="font-semibold mb-2">Indicators</h4>
            <ul className="ml-4 list-disc space-y-1">
                {indicators.map((indicator, i) => (
                    <li key={i}>
                        <strong>
                            {indicator.type}:</strong> {indicator.indicator}
                            </li>

                ))}

            </ul>
        </div>
    );
};
export default IndicatorList;