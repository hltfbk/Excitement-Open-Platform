USE `wikipedialexicalrule`;

truncate table patterncounters;

insert into patterncounters(pattern, patternType, patternCount)
SELECT POSPattern, 1, count(*) FROM rulepatterns
group by POSPattern;

insert into patterncounters(pattern, patternType, patternCount)
SELECT relationsPattern, 3, count(*) FROM rulepatterns
WHERE relationsPattern is not null
group by relationsPattern;

insert into patterncounters(pattern, patternType, patternCount)
SELECT POSrelationsPattern, 4, count(*) FROM rulepatterns
WHERE POSrelationsPattern is not null
group by POSrelationsPattern;

ALTER TABLE patterncounters
ADD INDEX patternVals (patternType ASC, pattern (255) ASC) ;

